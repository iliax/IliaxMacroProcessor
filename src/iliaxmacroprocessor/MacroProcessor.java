package iliaxmacroprocessor;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import static iliaxmacroprocessor.ParsingUtils.*;
import static iliaxmacroprocessor.MacrosCommand.*;

/**
 *  main algorithm class
 * @author iliax
 */
public class MacroProcessor {

    // позволять рекурсивный вызов макроса
    public static boolean enableRecursionMacrossCall = true;

    private static final Logger LOG = Logger.getLogger(MacroProcessor.class.getName());

    private List<String> _strings;
    
    private GuiConfig _guiConfig;

    private List<Macros> _macroses = new ArrayList<Macros>();

    private MacrosArgumentsParser _macrosArgumentsParser = new MacrosArgumentsParser();

    public MacroProcessor(GuiConfig guiConfig, List<String> strings) {
        _strings = strings;
        _guiConfig = guiConfig;     
    }

    public void start1stScan(){
        LOG.info("1st scan begun");

        currentMacrosesStack.add(Macros.ROOT_MACROS);

        Macros.ROOT_MACROS.getNestedMacroses().clear(); 

        int i = 0;
        int stringsSize = _strings.size();

        while (i < stringsSize){
            
            int check = checkMacroGenHeader(_strings.get(i));

            if(check != -1){
                i = processMacroGeneration(i, check);
            } else {
                i++;
            }
        }
        
        LOG.info("1st scan finished");
    }


    private int processMacroGeneration(int stringNum, int macroWordNum){

        String contextPrefix = "";
        
        contextPrefix = currentMacrosesStack.getLast().getName()+".";
        
        Macros newMacros = parseMacrosHeader(_strings.get(stringNum), macroWordNum);

        if(newMacros == null){
            throw new RuntimeException("invalid macros name! str " + stringNum);
        }
        
        newMacros.setName(contextPrefix + newMacros.getName());
        LOG.info("start of "+newMacros.getName()+" macros definition");

        currentMacrosesStack.add(newMacros);
        
        _macrosArgumentsParser.parseVariablesArea(_strings.get(stringNum), currentMacrosesStack.getLast());

        int i = stringNum + 1;
        while( ! checkMacroGenEnding(_strings.get(i))){
            String macroString = _strings.get(i);
            
            int checkHeader = checkMacroGenHeader(macroString);

            if(checkHeader == -1){
                LOG.info("adding new string: "+macroString);

                String lbl = getLabelFromString(macroString);
                if(lbl != null){
                    currentMacrosesStack.getLast().addLabel(lbl, i - stringNum);
                }

                newMacros.getStrings().add(macroString);

                i++;
            } else {
                LOG.info("starting nested macros definition");
                
                i = processMacroGeneration(i, checkHeader) + 1;
            }
        }

        if(_macroses.contains(newMacros)){
            throw new RuntimeException("this macros already defined");
        }
        
        _macroses.add(newMacros);  
        LOG.info("new macros added:\n" + newMacros.toString());

        currentMacrosesStack.getLast().getParentMacros().getNestedMacroses().add(currentMacrosesStack.pollLast());

        return i;
    }

    public void start2ndScan(){
        LOG.info("2nd scan started");
        StringBuilder text = new StringBuilder();
        
        int i=0;
        while(i < _strings.size()){
            String s = _strings.get(i);

            LOG.info("processing string: "+s);
            
            if(checkMacroGenHeader(s) != -1){
                i = passMacroDefinition(i);
                continue;
            }

            Macros m = checkMacroCall(s);
            
            if(m != null){
                processMacrosInjection(text, m , s);
                i++;
                continue;
            }

            text.append(s);
            text.append(LS);

            i++;
        }

        _guiConfig.outTextField.setText(text.toString());

        LOG.info("2nd scan finished");
    }

    private int passMacroDefinition(int begin){
        int i = begin + 1;

        while(!checkMacroGenEnding(_strings.get(i))){
            if(checkMacroGenHeader(_strings.get(i)) != -1){
                i = passMacroDefinition(i);
                continue;
            }
            i++;
        }

        return i + 1;
    }

    private Macros checkMacroCall(String str){

        List<String> lexems = getLexems(str);

        if((lexems.size() >= 2)
                && (getMacrosByName(lexems.get(0)) != null)){
            if(lexems.get(1).startsWith("["))
                return getMacrosByName(lexems.get(0));
        }

        if((lexems.size() >= 3) && (getMacrosByName(lexems.get(1)) != null)){
            if(lexems.get(2).startsWith("["))
                return getMacrosByName(lexems.get(1));
        }

        return null;
    }

    private LinkedList<Macros> currentMacrosesStack = new LinkedList<Macros>();

    private void processMacrosInjection(StringBuilder text, Macros macros, String begining){
        
        LOG.info("process macro injection: " + macros.getName());

        currentMacrosesStack.add(macros);

        try {
            _macrosArgumentsParser.setMacrosVars(begining, macros);
        } catch(RuntimeException e){
            LOG.error("err", e);
            throw e;
        }

        for(int i=0; i < macros.getStrings().size(); i++){

            String s = macros.getStrings().get(i);

            try {
                int check = processCommand(currentMacrosesStack.getLast(), i, macros.getStrings());
                i += check;
                continue;
            } catch(NoCommandException e){
                //go on!
            }

            s = replaceVarsByTheirValues(s, currentMacrosesStack.getLast());

            processMacroCall(s, text);

        }

        currentMacrosesStack.pollLast();
    }

    private void processMacroCall(String s, StringBuilder text){
         Macros m = checkMacroCall(s);
        if(m == null){
            text.append(s);
            text.append(LS);
        } else {

            if((!enableRecursionMacrossCall)
                    && currentMacrosesStack.contains(m)){
                text.append("//"+ s +" <-- warning!");
                text.append(LS);
                LOG.warn("parent macros call");
                //continue;  //TODO check this!!
            } else {
                processMacrosInjection(text, m, s);

            }
        }
    }

    public static String replaceVarsByTheirValues(String str, Macros currMacros){
        List<String> lexems = getLexems(str);
        List<String> toAppend = new ArrayList<String>();

        for(String lex : lexems){
            String varVal = currMacros.getVariables().getVariableVAlFromGlobalContext(lex);
            if(varVal != null){
                toAppend.add(varVal);
            } else {
                toAppend.add(lex);
            }
        }

        String s = Joiner.on(" ").join(toAppend);
        return s;
    }

    private Macros getMacrosByName(String name){

        if(!isValidMacrosName(name)){
            return null;
        }        

        String name_ = currentMacrosesStack.getLast().getName() + "." + name; 

        Macros parentForSearch = currentMacrosesStack.getLast();

        for(Macros m : parentForSearch.getNestedMacroses()){   // поиск сначала среди вложенных
            if(m.getName().equals(name_)){ 
                return m;
            }
        }
        
       /* name_ = parentForSearch.getParentMacros().getName() + "." +name;

        for(Macros m : parentForSearch.getParentMacros().getNestedMacroses()){   // поиск среди того же уровня
            if(m.getName().equals(name_)){
                return m;
            }
        }*/

//        //recursive check here by LIST recursion - FAIL
//        if( ! currentMacrosesStack.getLast().equals(Macros.ROOT_MACROS)){
//            Macros temp = currentMacrosesStack.pollLast();
//            Macros result = getMacrosByName(name);
//            currentMacrosesStack.add(temp);
//            return result;
//        }

        //iteration check by PARENTS
         Macros currMacros = currentMacrosesStack.getLast().getParentMacros();

         int par = 2;
         while( par > 0){  //для избежания зацикливания (root тоже надо обработать)
            name_ = currMacros.getName() + "." + name;

            for(Macros m : currMacros.getNestedMacroses()){
                if(m.getName().equals(name_)){
                    return m;
                }
            }

            currMacros = currMacros.getParentMacros();

            if(currMacros.getParentMacros().equals(Macros.ROOT_MACROS)){
                par --;
            }
         }

        return null;
    }

    private Macros parseMacrosHeader(String header, int macroWordNum){
         List<String> lexems = getLexems(header);

         if( isValidMacrosName(lexems.get(macroWordNum+1))){
            Macros newMacros = new Macros(lexems.get(macroWordNum+1), currentMacrosesStack.getLast());
            return newMacros;
         }

         return null;
    }

    public void logError(String mess){
        LOG.debug(mess);
    }


}
