

package iliaxmacroprocessor.gui;

import javax.swing.JButton;
import javax.swing.JTextPane;

/**
 *
 * @author iliax
 */
public class GuiConfig {

    public final JTextPane sourseTextField;

    public final JTextPane outTextField;

    public final JButton secScanButt, nextButt, endButt;

    public GuiConfig(JTextPane sourseTextField, JTextPane outTextField, JButton sec,JButton next,JButton end) {
        this.sourseTextField = sourseTextField;
        this.outTextField = outTextField;
        secScanButt = sec;
        nextButt = next;
        endButt = end;
    }

    
}
