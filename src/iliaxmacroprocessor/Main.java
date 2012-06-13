package iliaxmacroprocessor;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.sun.java_cup.internal.runtime.Symbol;
import iliaxmacroprocessor.gui.GuiConfig;
import iliaxmacroprocessor.gui.MainForm;
import iliaxmacroprocessor.logging.ConsoleAppenderImpl;
import iliaxmacroprocessor.logic.MacroProcessor;
import iliaxmacroprocessor.logic.TextDataHolder;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JTextPane;
import javax.swing.UIManager;
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
    public static void main(String[] aArgs) throws Exception {

        if (aArgs == null || aArgs.length == 0) {
            
            for (javax.swing.UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    try {
                        UIManager.setLookAndFeel(info.getClassName());
                    } catch (Exception ex) {
                    }
                    break;
                }
            }

            LOG.info("start");

            MainForm.main(aArgs);
        } else {

            List<String> args = Arrays.asList(aArgs);

            String out = "out.txt", in = "test.qtr";

            for (String s : args) {
                if (s.startsWith("--out=")) {
                    out = s.substring(s.indexOf("=")+1);
                } else if (s.startsWith("--in=")) {
                    in = s.substring(s.indexOf("=")+1);
                }
                if (s.equalsIgnoreCase("--showlog=false")) {
                    ConsoleAppenderImpl.APPEND_TO_CONSOLE = false;
                }
            }

            GuiConfig gc = new GuiConfig(new JTextPane(), new JTextPane(),
                    new JLabel(), new JButton(), new JButton(), new JList());

            TextDataHolder textDataHolder = null;
            try{
                textDataHolder = new TextDataHolder(new File(in));
            }catch(RuntimeException r){
                System.err.println("входного файла не существует! "+r.getMessage());
                return;
            }

            AtomicBoolean ab = new AtomicBoolean(true);

            MacroProcessor processor =
                    new MacroProcessor(gc, textDataHolder.getStrings(TextDataHolder.NO_EMPTY_STRINGS),
                    ab, textDataHolder);

            MacroProcessor.CONSOLE_MODE = true;
            processor.start1stScan();

            if (ConsoleAppenderImpl.APPEND_TO_CONSOLE == false) {
                System.out.print("OUT:\n" + gc.outTextField.getText());
            }

            Files.write(gc.outTextField.getText(), new File(out), Charsets.UTF_8);

        }
    }

}
