package iliaxmacroprocessor.logic;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.base.Splitter;
import iliaxmacroprocessor.gui.GuiConfig;
import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.log4j.Logger;
import static iliaxmacroprocessor.logic.ParsingUtils.*;
import static iliaxmacroprocessor.logic.MacrosCommand.*;

/**
 * main algorithm class
 * @author iliax
 */
public class MacroProcessor {

    // позволять рекурсивный вызов макроса
    public static boolean enableRecursionMacrossCall = true;

    private static final Logger LOG = Logger.getLogger(MacroProcessor.class.getName());

    public List<String> _strings;
    
    private GuiConfig _guiConfig;

    public List<Macros> _macroses = new ArrayList<Macros>();

    private MacrosArgumentsParser _macrosArgumentsParser = new MacrosArgumentsParser();

    private static AtomicBoolean atomicBoolean;

    StringBuffer text;

    public MacroProcessor(GuiConfig guiConfig, List<String> strings, AtomicBoolean ab) {
        _strings = strings;
        _guiConfig = guiConfig;
        atomicBoolean = ab;
    }

    public  void updateMAcrosesList(){

        String [] mNames = new String[_macroses.size()+1];
        for(int i = 0; i < _macroses.size(); i++){
            mNames[i] = _macroses.get(i).getName();
        }

        _guiConfig.macrosesList.setListData(mNames);
    }

    public void start1stScan(){
        updateMAcrosesList();

        LOG.info("==================================");
        LOG.info("   FIRST SCAN");
        LOG.info("==================================");

        LOG.info("1st scan begun");

        currentMacrosesStack.add(Macros.ROOT_MACROS);

        Macros.ROOT_MACROS.getNestedMacroses().clear(); 

        int i = 0;
        int stringsSize = _strings.size();

        while (i < stringsSize){

            LOG.info("checking strings: " +  _strings.get(i));
            int check = checkMacroGenHeader(_strings.get(i));

            tryLock();

            if(check != -1){
                i = processMacroDefinition(i, check);
            } else {
                LOG.info("passing string...");
                i++;
            }

            tryLock();
        }
        
        LOG.info("1st scan finished");

        _guiConfig.secScanButt.setVisible(true);

        updateMAcrosesList();

        atomicBoolean.set(false);
       

        LOG.info("==================================");
        LOG.info("   SECOND SCAN");
        LOG.info("==================================");

        tryLock();

        start2ndScan();

        LOG.info(LS + "== END ==");

        _guiConfig.secScanButt.setVisible(false);
        _guiConfig.nextButt.setEnabled(false);
        _guiConfig.endButt.setEnabled(false);
    }


    private int processMacroDefinition(int stringNum, int macroWordNum){

        String contextPrefix = "";
        
        contextPrefix = currentMacrosesStack.getLast().getName()+".";
        
        Macros newMacros = parseMacrosHeader(_strings.get(stringNum), macroWordNum);

        if(newMacros == null){
            throw new RuntimeException("invalid macros name! str " + stringNum);
        }
        
        newMacros.setName(contextPrefix + newMacros.getName());
        LOG.info("start of "+newMacros.getName()+" macros definition");

        tryLock();

        currentMacrosesStack.add(newMacros);
        
        _macrosArgumentsParser.parseVariablesArea(_strings.get(stringNum), currentMacrosesStack.getLast());

        int i = stringNum + 1;
        while( ! checkMacroGenEnding(_strings.get(i))){
            String macroString = _strings.get(i);
            
            int checkHeader = checkMacroGenHeader(macroString);

            if(checkHeader == -1){  // не обьявление нового макроса
                LOG.info("adding new string: "+macroString);

                tryLock();

                addLabel(macroString);

                newMacros.getStrings().add(macroString);

                i++;
            } else {
                LOG.info("starting nested macros definition");
                
                i = processMacroDefinition(i, checkHeader) + 1;
            }
        }

        if(_macroses.contains(newMacros)){
            throw new RuntimeException("this macros already defined");
        }
        
        _macroses.add(newMacros);  
        LOG.info("new macros added: " + newMacros.getName());
        updateMAcrosesList();
        tryLock();

        currentMacrosesStack.getLast().getParentMacros().getNestedMacroses().add(currentMacrosesStack.pollLast());

        return i;
    }

    private void addLabel(String str){
        List<String> lexems = getLexems(str);

        Macros currMacros = currentMacrosesStack.getLast();

        if(isValidMacroLabelName(lexems.get(0))){
            int shift = currMacros.getStrings().size()+1;
            if( ! currMacros.addLabel(lexems.get(0).substring(1, lexems.get(0).indexOf(":")), shift))
            {
                throw new RuntimeException("label "
                        + lexems.get(0) +" already defined");
            } else {
                LOG.info("label "+lexems.get(0)+" added");
                tryLock();
            }
        }
    }

    public void start2ndScan(){
        LOG.info("2nd scan started");
        
        text = new StringBuffer();

        int i=0;
        while(i < _strings.size()){
            String s = _strings.get(i);

            LOG.info("processing string: "+s);
            
            tryLock();

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

            appendText(s + LS);
            
            i++;
        }

        _guiConfig.outTextField.setText(text.toString());

        LOG.info("2nd scan finished");
        LOG.info("\n\n OUT:\n\n" + text.toString());
    }

    private int passMacroDefinition(int begin){
        LOG.info("passing macro definition");

        //tryLock();

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

    private void processMacrosInjection(StringBuffer text, Macros macros, String begining){
        /////////////////////////////////



//        //////////////////////////////
        
        LOG.info("process macros injection: " + macros.getName());
        tryLock();

        currentMacrosesStack.add(macros);
       
        _macrosArgumentsParser.setMacrosVars(begining, macros);

        for(int i=0; i < macros.getStrings().size(); i++){

            String s = macros.getStrings().get(i);
            LOG.info("processing string: " + s);

            try {
                int check = processCommand(macros, i);
                i += check;
                continue;
            } catch(NoCommandException e){
                //go on!
            }

            try {
                int check = processWhileCommand(i, text);
                i += check;
                continue;
            } catch(NoCommandException e ){
                //go on!
            }

            s = replaceVarsByTheirValues(s, currentMacrosesStack.getLast());

            processMacroCall(s, text);

        }

        currentMacrosesStack.pollLast();
    }

    // mutable bullshit
    private int processWhileCommand(int currentStr, StringBuffer text) throws NoCommandException {
        Macros currMacros = currentMacrosesStack.getLast();
        String whileHeader = currMacros.getStrings().get(currentStr);

        List<String> lexems = getLexems(whileHeader);

        if(!lexems.get(0).equals(WHILE)){
            throw new NoCommandException();
        }

        if((!whileHeader.contains("[")) || (!whileHeader.contains("]")) ){
            throw new NoCommandException();
        }

        LOG.info("processing WHILE command");
        tryLock();

        whileHeader = MacroProcessor.replaceVarsByTheirValues(whileHeader, currMacros);

        boolean ch = checkInequality(whileHeader.substring(whileHeader.indexOf("[")+1, whileHeader.indexOf("]")));
        if(ch == false){
            for(int i = currentStr + 1, j = 0; i < currMacros.getStrings().size(); i++, j++){
                if(currMacros.getStrings().get(i).contains(END_WHILE)){
                    LOG.info("passing WHILE block - inequality in [ ] wrong");
                    tryLock();
                    return j+1;
                }
            }
        } else {
            
            //////////////////////////////////
            for(int i = currentStr + 1; i < currMacros.getStrings().size(); i++){
                String s = currMacros.getStrings().get(i);

                if(s.contains(END_WHILE)){
                    LOG.info("WHILE processing finished");
                    tryLock();
                    
                    return -1; //чтобы остаться там же 
                }

                /////////////////////////////
                    try {
                        int check = processCommand(currMacros, i);
                        i += check;
                        continue;
                    } catch(NoCommandException e){
                        //go on!
                    }

                    try {
                        int check = processWhileCommand(i, text);
                        i += check;
                        continue;
                    } catch(NoCommandException e ){
                        //go on!
                    }

                    s = replaceVarsByTheirValues(s, currentMacrosesStack.getLast());

                    processMacroCall(s, text);
               //////////////////////////////////////////

            }

            //////////////////////////////////
        }
        
        throw new NoCommandException();
    }

    private void processMacroCall(String s, StringBuffer text){
        Macros m = checkMacroCall(s);
        if(m == null){
            LOG.info("appeding string: " + s);
            appendText(s + LS);
            tryLock();
        } else {

            if((!enableRecursionMacrossCall)
                    && currentMacrosesStack.contains(m)){
                appendText("//"+ s +" <-- warning!" + LS);

                LOG.warn("parent macros call");
                //continue;  //TODO check this!!
            } else {
                
                //иньектим строки
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

     static void tryLock(){
         if(atomicBoolean.get() == false){
            synchronized(MacroProcessor.class){
                try {
                    MacroProcessor.class.wait();
                } catch (InterruptedException ex) {
                }
             }
         }
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


    public synchronized  void appendText(String str){
        if(text == null || str.trim().isEmpty()){
            return;
        }

        //// deleting macro labels
        List<String> lexems = getLexems(str);
        if(isValidMacroLabelName(lexems.get(0))){
            str = str.replaceAll(lexems.get(0),"");
        }
        //

        //// adding _ to repeated labels
        if(isValidAssLabelName(lexems.get(0))){
            
            List<String> textLexems =
                    Lists.newArrayList(Splitter.on("\n")
                    .on(" ").trimResults().omitEmptyStrings().split(text.toString()));
            
            
            for(String  lex : Lists.reverse(textLexems)){
                if(lex.startsWith(lexems.get(0).substring(0, lexems.get(0).indexOf(":")))){
                    if(lex.contains(":")){
                        String repl = lex.substring(0, lex.indexOf(":")) + "_:";
                        str = str.replace(lexems.get(0), repl);

                            // LABELS processing in macros strings
                            Macros macros = currentMacrosesStack.getLast();
                            for(int i=0; i < macros.getStrings().size(); i++){
                                String macroStr = macros.getStrings().get(i);

                                List<String> mlexems = getLexems(macroStr);
                                List<String> toApp = new ArrayList<String>(mlexems);

                                for(int j=1; j < mlexems.size(); j++){
                                    if(isValidVariableName(mlexems.get(j))){
                                        if(lex.startsWith(mlexems.get(j))){
                                            String newLbl =
                                                    mlexems.get(j)+"_";
                                            toApp.set(j, newLbl);
                                        }
                                    }
                                }
                                macros.getStrings().set(i, Joiner.on(" ").join(toApp));
                            }
                            //

                        break;
                    }
                }
            }
        }
        //

        if(str.contains(LS)){
            checkIsStrValidAsseblerStr(str.substring(0, str.indexOf(LS)));
        } else {
            checkIsStrValidAsseblerStr(str);
        }
        
        str = str.trim();

        if(!str.isEmpty()){
            text.append(" " + str + LS);
        }
        
        _guiConfig.outTextField.setText(text.toString());

    }
}
