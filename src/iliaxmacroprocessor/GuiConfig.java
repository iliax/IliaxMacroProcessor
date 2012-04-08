/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package iliaxmacroprocessor;

import javax.swing.JTextArea;
import javax.swing.JTextPane;

/**
 *
 * @author iliax
 */
public class GuiConfig {

    public final JTextPane sourseTextField;

    public final JTextPane outTextField;


    public GuiConfig(JTextPane sourseTextField, JTextPane outTextField) {
        this.sourseTextField = sourseTextField;
        this.outTextField = outTextField;
    }

    
}
