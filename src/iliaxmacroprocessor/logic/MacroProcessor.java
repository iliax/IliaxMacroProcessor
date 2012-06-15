package iliaxmacroprocessor.logic;

import com.google.common.base.CharMatcher;
import com.google.common.collect.Lists;
import com.google.common.base.Splitter;
import iliaxmacroprocessor.gui.GuiConfig;
import com.google.common.base.Joiner;
import iliaxmacroprocessor.gui.MainForm;
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
    public static boolean enableRecursionMacrossCall = false;

    // пользовать $ перед переменными
    public static boolean enableUsingBuksBeforeVar = true;

    private static final Logger LOG = Logger.getLogger(MacroProcessor.class.getName());
    public static boolean CONSOLE_MODE = false;

    public List<String> _strings;
    
    private GuiConfig _guiConfig;

    public List<Macros> _macroses = new ArrayList<Macros>();

    private MacrosArgumentsParser _macrosArgumentsParser = new MacrosArgumentsParser();

    public static AtomicBoolean atomicBoolean;

    TextDataHolder textDataHolder;

    StringBuffer text;

    public MacroProcessor(GuiConfig guiConfig, List<String> strings, AtomicBoolean ab, TextDataHolder tdh) {
        _strings = strings;
        _guiConfig = guiConfig;
        atomicBoolean = ab;
        textDataHolder = tdh;
    }

    public  void updateMAcrosesList(){

        String [] mNames = new String[_macroses.size()+1];
        for(int i = 0; i < _macroses.size(); i++){
            mNames[i] = _macroses.get(i).getName();
        }

            _guiConfig.macrosesList.setListData(mNames);
    }

    public void start1stScan(){
        MainForm.appends = 0;
        stringsLimit = APPENDS_LIMIT;

        updateMAcrosesList();

        LOG.info("==================================");
        LOG.info("   FIRST SCAN");
        LOG.info("==================================");

        LOG.info("первый проход начался");

        currentMacrosesStack.add(Macros.ROOT_MACROS);

        Macros.ROOT_MACROS.getNestedMacroses().clear(); 

        int i = 0;
        int stringsSize = _strings.size();

        while (i < stringsSize){

            LOG.info("анализ строки: " +  _strings.get(i));
            int check = checkMacroGenHeader(_strings.get(i));

            tryLock();

            if(check != -1){
                i = processMacroDefinition(i, check, _strings);
            } else {
                LOG.info("пропускаем строку: "+_strings.get(i));
                i++;
            }

            tryLock();
        }
        
        LOG.info("первый проход закончился");

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


    private int processMacroDefinition(int stringNum, int macroWordNum, List<String> strs){

        String contextPrefix = "";
        
        contextPrefix = currentMacrosesStack.getLast().getName()+".";
        
        Macros newMacros = parseMacrosHeader(strs.get(stringNum), macroWordNum);

        if(newMacros == null){
            throw new RuntimeException("невалидное имя макроса. str " + stringNum);
        }
        
        newMacros.setName(contextPrefix + newMacros.getName());
        LOG.info("начало определения макроса "+newMacros.getName());

        tryLock();

        currentMacrosesStack.add(newMacros);
        
        _macrosArgumentsParser.parseVariablesArea(strs.get(stringNum), currentMacrosesStack.getLast());

        int i = stringNum + 1;
        int nested = 0;
        while( ! checkMacroGenEnding(strs.get(i))){
            String macroString = strs.get(i);
            
            int checkHeader = checkMacroGenHeader(macroString);

            if(checkHeader == -1){  // не обьявление нового макроса
                LOG.info("добавляем строку в макрос: "+macroString);

                tryLock();

                addLabel(macroString);

                newMacros.getStrings().add(macroString);

                i++;
            } else {
                // i = passMacroDefinition(i);
               
                //i = processMacroDefinition(i, checkHeader) + 1;
                
                newMacros.getStrings().add(macroString);
                nested++;
                
                i++;
            }

            if(nested != 0){
                if(checkMacroGenEnding(strs.get(i))){
                    newMacros.getStrings().add(strs.get(i));
                    nested--;
                    i++;
                }
            }


            //bullshit
            if(nested != 0){
                if(checkMacroGenEnding(strs.get(i))){
                    newMacros.getStrings().add(strs.get(i));
                    nested--;
                    i++;
                }
            }

            if(nested != 0){
                if(checkMacroGenEnding(strs.get(i))){
                    newMacros.getStrings().add(strs.get(i));
                    nested--;
                    i++;
                }
            }
            //bullshit ends
            
        }

        if(_macroses.contains(newMacros)){
            throw new RuntimeException("этот макрос уже определен");
        }
        
        _macroses.add(newMacros);  
        LOG.info("добавлен новый макрос: " + newMacros.getName());
        updateMAcrosesList();
        tryLock();

        currentMacrosesStack.getLast()
                .getParentMacros().getNestedMacroses().add(currentMacrosesStack.pollLast());

        return i;
    }


    private void addLabel(String str){
        List<String> lexems = getLexems(str);

        Macros currMacros = currentMacrosesStack.getLast();

        if(isValidMacroLabelName(lexems.get(0))){
            int shift = currMacros.getStrings().size()+1;
            if( ! currMacros.addLabel(lexems.get(0).substring(1, lexems.get(0).indexOf(":")), shift))
            {
                throw new RuntimeException("метка "
                        + lexems.get(0) +" уже существует");
            } else {
                LOG.info("метка "+lexems.get(0)+" добавлена");
                tryLock();
            }
        }
    }

    public void start2ndScan(){

        stringsLimit = 0;

        LOG.info("начало 2го прохода");
        
        text = new StringBuffer();

        int i=0;
        while(i < _strings.size()){
            String s = _strings.get(i);

            LOG.info("анализ строки: "+s);
            
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

        LOG.info("2й проход закончился");
        LOG.info("\n\n OUT:\n\n" + text.toString());
    }

    private int passMacroDefinition(int begin){
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

        if((lexems.size() >= 1)
                && (getMacrosByName(lexems.get(0)) != null)){
            
                return getMacrosByName(lexems.get(0));
        }

        if((lexems.size() >= 2) && (getMacrosByName(lexems.get(1)) != null)){
                return getMacrosByName(lexems.get(1));
        }

        return null;
    }


    private void processInnerMacrosesDefinition(Macros macros) {
        List<String> strs = macros.getStrings();
        for(int i=0; i< strs.size(); i++){
            String s = macros.getStrings().get(i);

            s = replaceVarsByTheirValues(s, macros);

            if(checkMacroGenHeader(s) != -1){
                i = processMacroDefinition(i, 0, strs);
            } else {
                try {
                    int check = processCommand(macros, i);
                    i += check;
                    continue;
                } catch(NoCommandException e){
                    //go on!
                }   
            } 
        }
    }


    private LinkedList<Macros> currentMacrosesStack = new LinkedList<Macros>();

    private void processMacrosInjection(StringBuffer text, Macros macros, String begining){

        LOG.info("начало макроподстановки: " + macros.getName());
        tryLock();
        
        currentMacrosesStack.add(macros);
        _macrosArgumentsParser.setMacrosVars(begining, macros);

        for(Macros m : macros.getNestedMacroses()){
            _macroses.remove(m);
        }
        macros.getNestedMacroses().clear();

        processInnerMacrosesDefinition(macros);
        updateMAcrosesList();
        
       
        for(int i=0; i < macros.getStrings().size(); i++){

            String s = macros.getStrings().get(i); 

             s = replaceVarsByTheirValues(s, currentMacrosesStack.getLast());

            if(checkMacroGenHeader(s) != -1){
                i = passNestedMacroDef(macros.getStrings(), i+1);
                i--;
                continue;
            }

            LOG.info("обработка строки: " + s);

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

           // s = replaceVarsByTheirValues(s, currentMacrosesStack.getLast()); //TODO is it right&

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

        LOG.info("обработка команды WHILE");
        tryLock();

        whileHeader = MacroProcessor.replaceVarsByTheirValues(whileHeader, currMacros);

        boolean ch =
                checkInequality(whileHeader.substring(whileHeader.indexOf("[")+1, whileHeader.indexOf("]")));

        int i = currentStr +1;

        

        if(ch == false){
            for(int i1 = currentStr + 1, j = 0; i1 < currMacros.getStrings().size(); i1++, j++){
                if(currMacros.getStrings().get(i1).contains(END_WHILE)){
                    LOG.info("пропуcк блока WHILE - выражение в скобках неверно");
                    tryLock();
                    return j+1;
                }
            }
        } else {

            //////////////////////////////////
            for(; i < currMacros.getStrings().size() ; i++){
                String s = currMacros.getStrings().get(i);

                
                if(s.contains(END_WHILE)){
                    LOG.info("конец итерации WHILE");
                    tryLock();
                        return -1; //чтобы остаться там же
                }

                s = replaceVarsByTheirValues(s, currentMacrosesStack.getLast());

                  if(checkMacroGenHeader(s) != -1){
                        i = passNestedMacroDef(currMacros.getStrings(), i+1);
                        i--;
                        continue;
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

                    //s = replaceVarsByTheirValues(s, currentMacrosesStack.getLast());

                    processMacroCall(s, text);
               //////////////////////////////////////////

            }



            //////////////////////////////////
        }


        return i - currentStr ;
        //throw new NoCommandException();  //TODO
    }

    private void processMacroCall(String s, StringBuffer text){
        Macros m = checkMacroCall(s);
        if(m == null){

            //LOG.info("пишем строку: " + s);

            
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
            String varVal = null;

            if(enableUsingBuksBeforeVar){
                if(lex.startsWith("$")){
                    varVal = currMacros.getVariables().getVariableVAlFromGlobalContext(lex.substring(1));
                    LOG.info("заменяем " + lex+ " ее значением "+varVal);
                } else if(lex.contains("$")){
                    varVal = lex.substring(0,
                            lex.indexOf("$")) +
                            currMacros.getVariables().getVariableVAlFromGlobalContext
                            (lex.substring(lex.indexOf("$")+1));
                    LOG.info("заменяем " + lex.substring(lex.indexOf("$")+1) + " ее значением "+varVal);
                }
            } else {
                varVal = currMacros.getVariables().getVariableVAlFromGlobalContext(lex);
            }
            
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
         if(CONSOLE_MODE == true){
            return;
         }

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
         

         if(textDataHolder.getAssCommndsNames().contains(lexems.get(0))){
             throw new RuntimeException("макрос не может называться как команда!: "+lexems.get(0));
         }

         if( isValidMacrosName(lexems.get(0))){
            Macros newMacros = new Macros(lexems.get(0), currentMacrosesStack.getLast());
            return newMacros;
         }

         return null;
    }

    public void logError(String mess){
        LOG.debug(mess);
    }


    public static volatile int APPENDS_LIMIT = 100;

    private int stringsLimit = 0;

    public void appendText(String str){
        try{
            if(++stringsLimit == APPENDS_LIMIT){
                throw new RuntimeException("слишком много строк!");
            }
            _appendText(str);
        } catch(IndexOutOfBoundsException e){
            throw new NotRealIOOfBException();
        }
    }

    public synchronized  void _appendText(String str){
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
            checkIsStrValidAsseblerStr(str.substring(0, str.indexOf(LS)), textDataHolder.getAssCommndsNames());
        } else {
            if(!str.trim().isEmpty()){
                checkIsStrValidAsseblerStr(str, textDataHolder.getAssCommndsNames());
            }
        }
        
        str = str.trim();

        if(!str.isEmpty()){
            text.append(" " + str + LS);
        }
        
        _guiConfig.outTextField.setText(text.toString());

    }

    private int passNestedMacroDef(List<String> strings, int i) {
        while(!checkMacroGenEnding(strings.get(i))){
            if(checkMacroGenHeader(strings.get(i)) != -1){
                i = passNestedMacroDef(strings, i+1);
                continue;
            }
            i++;
        }

        return i+1;
    }
    
}
