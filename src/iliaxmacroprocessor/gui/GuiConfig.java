

package iliaxmacroprocessor.gui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextPane;

/**
 *
 * @author iliax
 */
public class GuiConfig {

    public final JTextPane sourseTextField;

    public final JTextPane outTextField;

    public final JButton  nextButt, endButt;

    public final JLabel secScanButt;

    public final JList macrosesList;

    public GuiConfig(JTextPane sourseTextField, JTextPane outTextField, JLabel sec,JButton next,JButton end, JList lst) {
        this.sourseTextField = sourseTextField;
        this.outTextField = outTextField;
        secScanButt = sec;
        nextButt = next;
        endButt = end;
        macrosesList = lst;
    }

    
}
