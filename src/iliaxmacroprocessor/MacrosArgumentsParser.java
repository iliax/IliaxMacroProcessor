
package iliaxmacroprocessor;

import java.util.List;
import org.apache.log4j.Logger;

import  static iliaxmacroprocessor.ParsingUtils.*;
/**
 *
 * @author iliax
 */
public class MacrosArgumentsParser {

    private static final Logger LOG = Logger.getLogger(MacrosArgumentsParser.class.getName());

    public MacrosArgumentsParser() {
    }

    /** парсит параметры при макроопределении */
    public void parseVariablesArea(String str, Macros currentMacros) {
        if((!str.contains("[") || (!str.contains("]")))){
            throw new RuntimeException("this is not valid variables area!");
        }

        String argsArea = str.substring(str.indexOf("[")+1, str.indexOf("]")).trim();
        LOG.info("workng with args area: '"+argsArea+"'");

        List<String> lexems = ParsingUtils.getLexems(argsArea);

        for(String arg : lexems){

            if(!arg.contains("=")){
                //ключeвой
                if(!isValidVariableName(arg)){
                    throw new RuntimeException("invalid arg format");
                }
                currentMacros.getVariables().addVariable(arg);
            } else {
                //позиционный
                String varName = arg.substring(0, arg.indexOf("="));

                if(!isValidVariableName(varName)){
                    throw new RuntimeException("invalid arg format");
                }
                String varVal = arg.substring(arg.indexOf("=")+1);

                currentMacros.getVariables()
                        .addKeyVariable(varName,varVal);
            }

        }
    }
       

    public void setMacrosVars(String str, Macros m){
        if((!str.contains("[") || (!str.contains("]")))){
            throw new RuntimeException("this is not valid variables area!");
        }

        String argsArea = str.substring(str.indexOf("[")+1, str.indexOf("]")).trim();
        List<String> lexems = ParsingUtils.getLexems(argsArea);
        
        Macros.VariablesStore vs  = m.getVariables();
        int varCount = 0;
        
        boolean positionVarsEnded = false;
        for(String arg : lexems){

            if(isValidMacrosName(arg)){

                if(!arg.contains("=")){
                    //позиц
                    if(!positionVarsEnded){
                        if(varCount < m.getVariables().varSeqCount()){
                            vs.setVariableValue(varCount, arg);
                            varCount++;
                        } else {
                            throw new RuntimeException("too many args!");
                        }
                    } else {
                       throw new RuntimeException("position arg definition expected!");
                    }
                    
                } else {
                    //ключевой
                    positionVarsEnded =true;
                    String varName = arg.substring(0, arg.indexOf("="));
                    String varVal = arg.substring(arg.indexOf("=")+1); 

                    if(vs.isVariableExists(varName) && vs.isVariableKeyVar(varName)){
                        vs.setVariableValue(varName, varVal);
                    } else {
                        throw new RuntimeException("unknown contruction. Str: '"+str+"'");
                    }
                }
            }
        }

//        // если арг не передан то можно попробовать поискать его у родителя
//        if(varCount < vs.varSeqCount()){
//            for(int i = varCount; i < vs.varSeqCount(); i++){
//                String s = vs.getVariableVAlFromGlobalContext(vs.getVarsSequence().get(i));
//                if(s != null){
//                    vs.setVariableValue(i, s);
//                }
//            }
//        }
        
    }
}
