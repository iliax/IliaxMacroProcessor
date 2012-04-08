/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iliaxmacroprocessor;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.regex.Pattern;

/**
 *  main algorithm class
 * @author iliax
 */
public class MacroProcessor {

    private List<String> _strings;
    private GuiConfig _guiConfig;

    public MacroProcessor(GuiConfig guiConfig, List<String> strings) {
        _strings = strings;
        _guiConfig = guiConfig;
    }

    /** return words between " " */
    static List<String> getLexems(String str) {
        return Lists.newArrayList(Splitter.on(" ").trimResults().omitEmptyStrings().split(str));
    }

    static boolean isLabel(String lbl){
        if( Pattern.compile("^[A-Z_a-z]+([A-Za-z0-9_]){0,15}$").matcher(lbl).matches() ){
            return true;
        }

        return false;
    }

    
}
