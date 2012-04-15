
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

    public static final String IF = "IF";
    public static final String END_IF = "ENDIF";

    public static final String WHILE = "WHILE";
    public static final String END_WHILE = "ENDW";

    public static final String GOTO = "GOTO";

    private static void _incVarValue(String varName, Macros context, int sh){
  
        String variableVAlFromGlobalContext = context.getVariables().getVariableVAlFromGlobalContext(varName);

        if(variableVAlFromGlobalContext != null){
            try {
                int num = Integer.parseInt(variableVAlFromGlobalContext);
                num+= sh;
                
                _setVariableVaue(varName, num+"", context);

            } catch (NumberFormatException nfe){
                throw new RuntimeException("this variable is NOT INT");
            }
        } else {
            throw new RuntimeException("no variable with this name");
        }   
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

    public static int  processCommand(Macros context, int currentMacrosLine, List<String> macrosStrings)
            throws NoCommandException {
        
        List<String> lexems = getLexems(macrosStrings.get(currentMacrosLine));

        if(lexems.get(0).equals(SET) && lexems.size() == 3){
            _setVariableVaue(lexems.get(1), lexems.get(2), context);
            return 0;
        }

        if(lexems.get(0).equals(INC) && lexems.size() == 2){
            _incVarValue(lexems.get(1), context, 1);
            return 0;
        }

        if(lexems.get(0).equals(DEC) && lexems.size() == 2){
            _incVarValue(lexems.get(1), context, -1);
            return 0;
        }

        if(lexems.get(0).equals(IF) && lexems.size() >= 2){
            return _processIfCommand(currentMacrosLine, macrosStrings, context);
        }
        if(lexems.get(0).equals(END_IF)){
            return 0;
        }

        throw new NoCommandException();
    }

    public static int _processIfCommand(int currStr, List<String> macrosStrings, Macros context )
            throws NoCommandException {
        
        String str = macrosStrings.get(currStr);

        if((!str.contains("[")) || (!str.contains("]")) ){
            throw new NoCommandException();
        }

        String s = MacroProcessor.replaceVarsByTheirValues(str, context);

        boolean check = checkInequality(s.substring(s.indexOf("[")+1, s.indexOf("]")));
        if(check == true){
            return 0;
        } else {
            int count = 0;
            int i = currStr +1;
            while(true){
                List<String> lexems = getLexems(macrosStrings.get(i)); //TODO index out of bounds exc

                if(lexems.get(0).equals(END_IF)){
                    count++;
                    break;
                }
                count++;
                i++;
            }
            return count;
        }

    }

    public static class NoCommandException extends Exception {}

}
