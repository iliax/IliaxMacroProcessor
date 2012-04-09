
package iliaxmacroprocessor;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *  main algorithm class
 * @author iliax
 */
public class MacroProcessor {

    private static final Logger LOG = Logger.getLogger(MacroProcessor.class.getName());

    private static final String LS = System.getProperty("line.separator");

    private List<String> _strings;
    
    private GuiConfig _guiConfig;

    private List<Macros> _macroses = new ArrayList<Macros>();

    final String MACRO_DEF = "MACRO";

    final String MACRO_END = "MEND";

    public MacroProcessor(GuiConfig guiConfig, List<String> strings) {
        _strings = strings;
        _guiConfig = guiConfig;
        
    }

    /** return words between " " */
    static List<String> getLexems(String str) {
        return Lists.newArrayList(Splitter.on(" ").trimResults().omitEmptyStrings().split(str));
    }

    static boolean isLabel(String lbl){
        if( Pattern.compile("^[A-Z_a-z]+([A-Za-z0-9_]){0,15}:$").matcher(lbl).matches() ){
            return true;
        }

        return false;
    }

    public void start1stScan(){
        LOG.info("1st scan begun");

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
        
        LOG.info("1st scan ended");
    }

    private Macros currentMacros = Macros.ROOT_MACROS;

    private int processMacroGeneration(int stringNum, int macroWordNum){

        String contextPrefix = "";
        if( ! currentMacros.equals(Macros.ROOT_MACROS)){
            contextPrefix = currentMacros.getName()+".";
        }

        Macros newMacros = parseMacrosHeader(_strings.get(stringNum), macroWordNum);

        if(newMacros == null){
            //TODO
            throw new RuntimeException("invalid macros name! str " + stringNum);
        }
        
        newMacros.setName(contextPrefix + newMacros.getName());
        LOG.info("start of "+newMacros.getName()+" macros definition");

        currentMacros = newMacros;
        parseVariablesArea(_strings.get(stringNum));

        int i = stringNum + 1;
        while( ! checkMacroGenEnding(_strings.get(i))){
            String macroString = _strings.get(i);
            
            int checkHeader = checkMacroGenHeader(macroString);

            if(checkHeader == -1){
                LOG.info("adding new string: "+macroString);

                newMacros.getStrings().add(macroString);
                i++;
            } else {
                LOG.info("starting nested macros definition");
                
                i = processMacroGeneration(i, checkHeader) + 1;
            }
        }

        _macroses.add(newMacros);
        LOG.info("new macros added:\n" + newMacros.toString());

        currentMacros = currentMacros.getParentMacros();

        currentMacros.getNestedMacroses().add(newMacros);

        return i;
    }

    public void start2ndScan(){
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

            text.append(s + LS);

            i++;
        }

        _guiConfig.outTextField.setText(text.toString());
    }

    int passMacroDefinition(int begin){
        int i = begin + 1;

        //TODO cycle!
        while(!checkMacroGenEnding(_strings.get(i))){
            if(checkMacroGenHeader(_strings.get(i)) != -1){
                i = passMacroDefinition(i);
                continue;
            }

            i++;
        }

        return i + 1;
    }

    //TODO параметры
    Macros checkMacroCall(String str){

        List<String> lexems = getLexems(str);

        if((lexems.size() >= 2) && (getMacrosByName(lexems.get(0)) != null)){
            if(lexems.get(1).startsWith("["))
                return getMacrosByName(lexems.get(0));
        }

        if((lexems.size() >= 3) && (getMacrosByName(lexems.get(1)) != null)){
            if(lexems.get(2).startsWith("["))
                return getMacrosByName(lexems.get(1));
        }

        return null;
    }

    void processMacrosInjection(StringBuilder text, Macros macros, String begining){
        
        LOG.info("process macro injection: " + macros.getName());

        currentMacros = macros;

        if(!setMacrosVars(begining, macros)){
            //TODO err
        }

        for(String s : macros.getStrings()){

            List<String> lexems = getLexems(s);
            List<String> toAppend = new ArrayList<String>();
            
            for(String lex : lexems){
                String varVal = currentMacros.getVariables().getVariableVAlFromGlobalContext(lex);
                if(varVal != null){
                    toAppend.add(varVal);
                } else {
                    toAppend.add(lex);
                }
            }

            s = Joiner.on(" ").join(toAppend);      ///TODO test it

            Macros m = checkMacroCall(s);
            if(m == null){
                text.append(s + LS);
            } else {
                processMacrosInjection(text, m, s);
            }
        }

        currentMacros = currentMacros.getParentMacros();
    }

    Macros getMacrosByName(String name){

        if(!isValidMacrosName(name)){
            return null;
        }

        if( ! currentMacros.equals(Macros.ROOT_MACROS)){
            String _name = "." + name;

            for(Macros m : currentMacros.getNestedMacroses()){   // поиск среди вложенных
                if(m.getName().endsWith(_name)){
                    return m;
                }
            }
            
        } 

        for(Macros m : _macroses){
            if(name.equals(m.getName())){
                return m;
            }
        }

        return null;
    }

    private Macros parseMacrosHeader(String header, int macroWordNum){
         List<String> lexems = getLexems(header);

         if( isValidMacrosName(lexems.get(macroWordNum+1))){
            Macros newMacros = new Macros(lexems.get(macroWordNum+1), currentMacros);
            return newMacros;
         }

         return null;
    }

    private boolean isValidMacrosName(String name){
        // TODO stub here
        return true;
    }

    /** returns -1 if this is not macro generation header */
    private int checkMacroGenHeader(String head){
        List<String> lexems = getLexems(head);

        if(lexems.size() >= 3 && lexems.get(0).equals(MACRO_DEF) && lexems.get(2).startsWith("[")){
            return 0;
        } else if(lexems.size() >= 4 && lexems.get(1).equals(MACRO_DEF) && lexems.get(3).startsWith("[")){
            return 1;
        } else {
           return -1;
        }
   
    }

    private boolean checkMacroGenEnding(String end){
        List<String> lexems = getLexems(end);

        if(lexems.get(0).equals(MACRO_END)
                || (lexems.size() == 2 && lexems.get(1).equals(MACRO_END))){
            return true;
        } else {
            return false;
        }
    }

    public void logError(String mess){
        LOG.debug(mess);
    }

    public boolean setMacrosVars(String str, Macros m){
        if((!str.contains("[") || (!str.contains("]")))){
            return false;  // TODO throw exc here
        }

        String argsArea = str.substring(str.indexOf("[")+1, str.indexOf("]")).trim();

        List<String> lexems = getLexems(argsArea);

        int varCount = 0;
        //List<String> varSeq = m.getVariables().getVarsSequence();
        Macros.VariablesStore vs  = m.getVariables();

        boolean positionVarsEnded = false;
        for(String arg : lexems){
            if(isValidMacrosName(arg)){
                if(!arg.contains("=")){  //позиц
                    if(!positionVarsEnded){
                        if(varCount < m.getVariables().varSeqCount()){
                            vs.setVariableValue(varCount, arg);
                            varCount++;
                        } else {
                            return false; //TODO exc
                        }
                    }
                } else {        //ключевой
                    positionVarsEnded =true;
                    String varName = arg.substring(0, arg.indexOf("="));
                    String varVal = arg.substring(arg.indexOf("=")+1);

                    if(vs.isVariableExists(varName) && vs.isVariableKeyVar(varName)){
                        vs.setVariableValue(varName, varVal);
                    } else {
                        return false; //TODO exc
                    }
                }
            }
        }

        return true;
        
    }

    public boolean parseVariablesArea(String str) {
        if((!str.contains("[") || (!str.contains("]")))){
            return false;  // TODO throw exc here
        }

        String argsArea = str.substring(str.indexOf("[")+1, str.indexOf("]")).trim();
        LOG.info("workng with args area: '"+argsArea+"'");

        List<String> lexems = getLexems(argsArea);

        for(String arg : lexems){
            if(isValidVariableName(arg)){
                if(!arg.contains("=")){
                    currentMacros.getVariables().addVariable(arg);
                } else {
                    String varName = arg.substring(0, arg.indexOf("="));
                    String varVal = arg.substring(arg.indexOf("=")+1);
                    currentMacros.getVariables()
                            .addKeyVariable(varName,varVal);
                }
            } else {
                //TODO exc here
                return false;
            }
        }
        
        return true;
    }


    public boolean isValidVariableName(String varName){
        //TODO write it
        return true;
    }

}
