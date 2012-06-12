package iliaxmacroprocessor.logic;

import java.util.List;
import org.apache.log4j.Logger;

import  static iliaxmacroprocessor.logic.ParsingUtils.*;
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

        str = str.replace(",", " ");

        List<String> lexems = ParsingUtils.getLexems(str);
        lexems.remove(0);
        lexems.remove(0);
        
        LOG.info("анализ аргументов: "+lexems+"");

        for(String arg : lexems){

            if(!arg.contains("=")){
                //ключeвой
                if(!isValidVariableName(arg)){
                    throw new RuntimeException("неправильный формат аргумета: "+arg);
                }
                currentMacros.getVariables().addVariable(arg, true);
            } else {
                //позиционный
                String varName = arg.substring(0, arg.indexOf("="));

                if(!isValidVariableName(varName)){
                    throw new RuntimeException("неправильный формат аргумета: "+varName);
                }
                String varVal = arg.substring(arg.indexOf("=")+1);

                currentMacros.getVariables()
                        .addKeyVariable(varName,varVal);
            }

        }
    }
       

    public void setMacrosVars(String str, Macros m){
        /*if((!str.contains("[") || (!str.contains("]")))){
            throw new RuntimeException("это не валидная область аргументов!");
        }*/

        if(m.getParentMacros() != Macros.ROOT_MACROS){
            str = str.replace(m.getName().substring(m.getName().lastIndexOf(".")+1), "@");
        } else {
            str = str.replace(m.getName().substring(1), "@");
        }
        str = str.substring(str.indexOf("@") + 1 );

        str = str.replace(",", " ");

        String argsArea = str.replace("[", "").replace("]", "").trim();
        List<String> lexems = ParsingUtils.getLexems(argsArea.trim());
        
        Macros.VariablesStore vs  = m.getVariables();
        int varCount = 0;
        
        boolean positionVarsEnded = false;
        /*if(m.getVariables().getVarsSequence().isEmpty()){
            positionVarsEnded = true;
        }*/
        for(String arg : lexems){

            if(!arg.contains("=")){
                //позиц
                if(!positionVarsEnded){

                    if(varCount < m.getVariables().varSeqCount()){
                        vs.setVariableValue(varCount, arg);
                        varCount++;
                    } else {
                        throw new RuntimeException("слишком много агрументов!");
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
                    throw new RuntimeException("неопознанная конструкция. Str: '"+str+"'");
                }
            }
            
        }

        if(varCount < vs.varSeqCount()){
            throw new RuntimeException("мало аргументов в вызове макроса");
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
