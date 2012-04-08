package iliaxmacroprocessor;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

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

        List<String> tst2 = MacroProcessor.getLexems("a b   c");

        assertTrue(tst.equals(tst2));
    }

    @Test
    public void isLabel(){
        assertTrue(MacroProcessor.isLabel("L1:"));
        assertFalse(MacroProcessor.isLabel("L1"));
    }
}