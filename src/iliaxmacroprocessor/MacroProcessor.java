
package iliaxmacroprocessor;

import com.google.common.base.Joiner;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import  static iliaxmacroprocessor.ParsingUtils.*;

/**
 *  main algorithm class
 * @author iliax
 */
public class MacroProcessor {

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
        
        _macrosArgumentsParser.parseVariablesArea(_strings.get(stringNum), currentMacros);

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

            text.append(s);
            text.append(LS);

            i++;
        }

        _guiConfig.outTextField.setText(text.toString());
    }

    int passMacroDefinition(int begin){
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

        try{
            _macrosArgumentsParser.setMacrosVars(begining, macros);
        } catch(RuntimeException e){
            LOG.error("err", e);
            throw e;
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



    public void logError(String mess){
        LOG.debug(mess);
    }


}
