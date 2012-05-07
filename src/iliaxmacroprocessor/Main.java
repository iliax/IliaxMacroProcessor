package iliaxmacroprocessor;

import iliaxmacroprocessor.gui.MainForm;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import org.apache.log4j.Logger;

/**
 *
 * @author iliax
 */
public class Main {

    private static final Logger LOG = Logger.getLogger(Main.class.getName());
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
         if(args == null || args.length == 0){
            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    try {
                        UIManager.setLookAndFeel(info.getClassName());
                    } catch (Exception ex) {
                    }
                    break;
                }
            }

            LOG.info("start");

            MainForm.main(args);
        } else {
         //console mode here...
        }
    }
}
