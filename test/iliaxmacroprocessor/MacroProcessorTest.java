package iliaxmacroprocessor;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

import  static iliaxmacroprocessor.ParsingUtils.*;
/**
 *
 * @author iliax
 */
public class MacroProcessorTest {

    MacroProcessor nullMacroProc = new MacroProcessor(null, null);

    public MacroProcessorTest() {
    }

    @Test
    public void getLexems(){
        List<String> tst = ImmutableList.of("a", "b", "c");

        List<String> tst2 = ParsingUtils.getLexems("a b   c");

        assertTrue(tst.equals(tst2));
    }

    @Test
    public void isLabel(){
        assertTrue(ParsingUtils.isValidLabelName("L1:"));
        assertFalse(ParsingUtils.isValidLabelName("L1"));
    }

    @Test
    public void parseVariablesArea(){
        String arg = "q1=2";
       assertEquals(arg.substring(0, arg.indexOf("=")), "q1");
       assertEquals(arg.substring(arg.indexOf("=")+1), "2");
    }
}