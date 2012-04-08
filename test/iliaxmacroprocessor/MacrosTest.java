/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package iliaxmacroprocessor;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author iliax
 */
public class MacrosTest {

    public MacrosTest() {
    }

    @Test
    public void test1() {
        Macros m = new Macros("mac1", MacrosContext.ROOT_MACROS_CONTEXT);
        System.out.println(m.getContext());

        assertTrue(m.getContext().equals(MacrosContext.ROOT_MACROS_CONTEXT));

        assertTrue(MacrosContext.ROOT_MACROS_CONTEXT.equals(MacrosContext.ROOT_MACROS_CONTEXT));
    }
    
}
