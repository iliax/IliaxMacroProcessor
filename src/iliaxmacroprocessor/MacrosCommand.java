
package iliaxmacroprocessor;
import java.util.List;
import  static iliaxmacroprocessor.ParsingUtils.*;
/**
 *
 * @author iliax
 */
public class MacrosCommand {

    public static final String SET = "SET";

    public static final String DEC = "DEC";

    public static final String INC = "INC";

    private static void _incVarValue(String varName, Macros context){
  
        String variableVAlFromGlobalContext = context.getVariables().getVariableVAlFromGlobalContext(varName);

        if(variableVAlFromGlobalContext != null){
            try {
                int num = Integer.parseInt(variableVAlFromGlobalContext);
                num++;
                
                _setVariableVaue(varName, num+"", context);

            } catch (NumberFormatException nfe){
                throw new RuntimeException("this variable is NOT INT");
            }
        } else {
            throw new RuntimeException("no variable with this name");
        }   
    }

    /** COPYPASTE HERE!!! */
    private static  void _decVarValue(String varName, Macros context){
        _incVarValue(varName, context);
    }

    private  static void _setVariableVaue(String varName, String val, Macros context){
        
        if(!isValidVariableName(varName)){
            return;
        }

        if(context.getVariables().getVariableVAlFromGlobalContext(varName) != null){
            context.getVariables().setVariableValueInGlobalContext(varName, val);
        } else {
            context.getVariables().addVariable(varName);
            context.getVariables().setVariableValue(varName, val);
        }
    }

    public  static boolean processCommand(String str, Macros context){
        List<String> lexems = getLexems(str);

        if(lexems.get(0).equals(SET) && lexems.size() == 3){
            _setVariableVaue(lexems.get(1), lexems.get(2), context);
            return true;
        }

        if(lexems.get(0).equals(INC) && lexems.size() == 2){
            _incVarValue(lexems.get(1), context);
            return true;
        }

        if(lexems.get(0).equals(DEC) && lexems.size() == 2){
            _decVarValue(lexems.get(1), context);
            return true;
        }

        return false;
    }

    
}
