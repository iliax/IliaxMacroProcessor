package iliaxmacroprocessor;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.apache.log4j.Logger;
import static iliaxmacroprocessor.ParsingUtils.*;

/**
 *  main algorithm class
 * @author iliax
 */
public class MacroProcessor {

    // позволять рекурсивный вызов макроса
    public boolean enableRecursionMacrossCall = false;

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

        Macros.ROOT_MACROS.getNestedMacroses().clear(); //TODO 

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
            //TODO
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

        _macroses.add(newMacros);   //TODO unique check
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

        for(String s : macros.getStrings()){

            List<String> lexems = getLexems(s);
            List<String> toAppend = new ArrayList<String>();
            
            for(String lex : lexems){
                String varVal = currentMacrosesStack.getLast().getVariables().getVariableVAlFromGlobalContext(lex);
                if(varVal != null){
                    toAppend.add(varVal);
                } else {
                    toAppend.add(lex);
                }
            }

            s = Joiner.on(" ").join(toAppend);      ///TODO test it

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
                    continue;
                } else {
                    processMacrosInjection(text, m, s);

                }
            }
        }

        currentMacrosesStack.pollLast();
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

        /*name_ = parentForSearch.getParentMacros().getName() + "." +name;

        for(Macros m : parentForSearch.getParentMacros().getNestedMacroses()){   // поиск среди того же уровня
            if(m.getName().equals(name_)){
                return m;
            }
        }*/

        //recursive check here
        if( ! currentMacrosesStack.getLast().equals(Macros.ROOT_MACROS)){
            if(!currentMacrosesStack.getLast().isBrotherTo(parentForSearch)){
                System.err.println("olololo\n\n");
                return null;
            }
            Macros temp = currentMacrosesStack.pollLast();
            Macros result = getMacrosByName(name);
            currentMacrosesStack.add(temp);
            return result;
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
