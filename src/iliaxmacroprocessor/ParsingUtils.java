
package iliaxmacroprocessor;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.regex.Pattern;
/**
 *
 * @author iliax
 */
public class ParsingUtils {

    public static final String LS = System.getProperty("line.separator");

    static final String MACRO_DEF = "MACRO";
    static final String MACRO_END = "MEND";

    /** bad code*/
    public static INEQUALITY isInequality(String str){

        if(str.contains(">") && !str.contains("=") && !str.contains("<")){
           if(Lists.newArrayList(Splitter.on(">").trimResults().omitEmptyStrings().split(str)).size() == 2){
               return INEQUALITY.GT;
           }
        }

        if(str.contains("<") && !str.contains("=") && !str.contains(">")){
           if(Lists.newArrayList(Splitter.on("<").trimResults().omitEmptyStrings().split(str)).size() == 2){
               return INEQUALITY.LT;
           }
        }

        if(str.contains("==") && !str.contains(">") && !str.contains("<")){
           if(Lists.newArrayList(Splitter.on("==").trimResults().omitEmptyStrings().split(str)).size() == 2){
               return INEQUALITY.EQ;
           }
        }

        if(str.contains("!=") && !str.contains(">") && !str.contains("<")){
           if(Lists.newArrayList(Splitter.on("!=").trimResults().omitEmptyStrings().split(str)).size() == 2){
               return INEQUALITY.NOT_EQ;
           }
        }

        return INEQUALITY.EMPTY;
    }

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

    public static List<String> getLexemsSplittedBy(String str, String splitter){
        return Lists.newArrayList(Splitter.on(splitter).trimResults().omitEmptyStrings().split(str));
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
        if(name.startsWith(".")){   // we need it?
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
                 return lexems.get(0).substring(0, lexems.get(0).indexOf(":"));
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

    public static boolean checkStringByPattern(String str, String pattern){
        return Pattern.compile(pattern).matcher(str).matches() ;
    }


    /** bad code */
    public static  boolean checkInequality(String str){

        str = str.trim();

        INEQUALITY inequalityType = isInequality(str);

        if(inequalityType == INEQUALITY.EMPTY){
            throw new RuntimeException("this is not inequality!");
        }

        List<String> lexems;

        if(inequalityType == INEQUALITY.EQ){
            lexems = getLexemsSplittedBy(str, "==");
            if(lexems.get(0).trim().equals(lexems.get(1).trim())){
                return true;
            } else {
                return false;
            }
        }

        if(inequalityType == INEQUALITY.NOT_EQ){
            lexems = getLexemsSplittedBy(str, "!=");
            if( ! lexems.get(0).trim().equals(lexems.get(1).trim())){
                return true;
            } else {
                return false;
            }
        }

        if(inequalityType == INEQUALITY.GT){
            lexems = getLexemsSplittedBy(str, ">");
            Pair p = getNumberPairByLexems(lexems);
            if(p == null){
                return false;
            }
            return p.A > p.B;
        }

        if(inequalityType == INEQUALITY.LT){
            lexems = getLexemsSplittedBy(str, "<");
            Pair p = getNumberPairByLexems(lexems);
            if(p == null){
                return false;
            }
            return p.A < p.B;
        }

        return false;
    }

    private static Pair getNumberPairByLexems(List<String> lexems){
        try {
            return new Pair(Integer.parseInt(lexems.get(0)), Integer.parseInt(lexems.get(1)));
        } catch(NumberFormatException nfe){
            return null;
        }
    }

    private static class Pair {
        public final int A;
        public final int B;

        public Pair(int a1, int a2) {
            this.A = a1;
            this.B = a2;
        }
    }
}
