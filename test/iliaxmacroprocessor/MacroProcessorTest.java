package iliaxmacroprocessor;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import iliaxmacroprocessor.logic.INEQUALITY;
import iliaxmacroprocessor.logic.ParsingUtils;
import iliaxmacroprocessor.logic.MacroProcessor;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author iliax
 */
public class MacroProcessorTest {

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
        assertTrue(ParsingUtils.isValidMacroLabelName("L1:"));
        assertFalse(ParsingUtils.isValidMacroLabelName("L1"));
    }

    @Test
    public void parseVariablesArea(){
        String arg = "q1=2";
       assertEquals(arg.substring(0, arg.indexOf("=")), "q1");
       assertEquals(arg.substring(arg.indexOf("=")+1), "2");
    }

    @Test
    public void isInequalityTest(){
        assertTrue(ParsingUtils.isInequality("3>3") == INEQUALITY.GT);
        assertTrue(ParsingUtils.isInequality("3<3") == INEQUALITY.LT);
        assertTrue(ParsingUtils.isInequality("3 > 3") == INEQUALITY.GT);
        assertTrue(ParsingUtils.isInequality("3 != 3") == INEQUALITY.NOT_EQ);
        assertTrue(ParsingUtils.isInequality("3==3") == INEQUALITY.EQ);
        assertFalse(ParsingUtils.isInequality("3 >= 3") == INEQUALITY.GT);
        assertTrue(ParsingUtils.isInequality("3== 3 3") == INEQUALITY.EQ);

        assertTrue(ParsingUtils.checkInequality("3 == 3"));
        assertTrue(ParsingUtils.checkInequality("qwe==qwe"));
        assertFalse(ParsingUtils.checkInequality("32 == 3"));
        assertTrue(ParsingUtils.checkInequality("3 < 23"));
        assertTrue(ParsingUtils.checkInequality("123>33"));
        assertTrue(ParsingUtils.checkInequality("3 != 32"));
        assertFalse(ParsingUtils.checkInequality("3 != 3"));
    }


}