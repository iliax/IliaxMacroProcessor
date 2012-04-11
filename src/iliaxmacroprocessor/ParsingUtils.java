
package iliaxmacroprocessor;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author iliax
 */
public class ParsingUtils {

    static final String MACRO_DEF = "MACRO";

    public static final String LS = System.getProperty("line.separator");

    static final String MACRO_END = "MEND";

    public static boolean checkMacroGenEnding(String end){
        List<String> lexems = getLexems(end);

        if(lexems.get(0).equals(MACRO_END)
                || (lexems.size() == 2 && lexems.get(1).equals(MACRO_END))){
            return true;
        } else {
            return false;
        }
    }

        /** return words between " " */
    public static List<String> getLexems(String str) {
        return Lists.newArrayList(Splitter.on(" ").trimResults().omitEmptyStrings().split(str));
    }


    public static boolean isValidLabelName(String lbl){
        if( Pattern.compile("^[A-Z_a-z]+([A-Za-z0-9_]){0,15}:$").matcher(lbl).matches() ){
            return true;
        }

        return false;
    }

        /** returns -1 if this is not macro generation header */
    public static int checkMacroGenHeader(String head){
        List<String> lexems = getLexems(head);

        if(lexems.size() >= 3 && lexems.get(0).equals(MACRO_DEF) && lexems.get(2).startsWith("[")){
            return 0;
        } else if(lexems.size() >= 4 && lexems.get(1).equals(MACRO_DEF) && lexems.get(3).startsWith("[")){
            return 1;
        } else {
           return -1;
        }

    }

    public static boolean isValidMacrosName(String name){
        if(name.startsWith(".")){
            return isValidMacrosName(name.substring(1));
        }

        if( Pattern.compile("^[A-Z_a-z]+([A-Za-z0-9_]){0,15}$").matcher(name).matches() ){
            return true;
        }

        return false;
    }

    public static String getLabelFromString(String str){
         List<String> lexems = getLexems(str);

         if( ! lexems.isEmpty()){
             if(isValidLabelName(lexems.get(0))){
                 return lexems.get(0);
             }
         }

         return null;
    }

    public static boolean isValidVariableName(String varName){
        if( Pattern.compile("^[A-Z_a-z]+([A-Za-z0-9_]){0,15}$").matcher(varName).matches() ){
            return true;
        }
        
        return false;
    }
}
