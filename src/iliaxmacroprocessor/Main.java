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
    public static void main(String[] aArgs) throws Exception {

        test();

        if (aArgs == null || aArgs.length == 0) {
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

            MainForm.main(aArgs);
        } else {

            List<String> args = Arrays.asList(aArgs);

            String out = "out.txt", in = "test.qtr";

            for (String s : args) {
                if (s.startsWith("--out=")) {
                    out = s.substring(s.lastIndexOf("--out="));
                } else if (s.startsWith("--in=")) {
                    in = s.substring(s.lastIndexOf("--in="));
                }
                if (s.equalsIgnoreCase("--showlog=false")) {
                    ConsoleAppenderImpl.APPEND_TO_CONSOLE = false;
                }
            }

            GuiConfig gc = new GuiConfig(new JTextPane(), new JTextPane(),
                    new JLabel(), new JButton(), new JButton(), new JList());

            TextDataHolder textDataHolder = new TextDataHolder(new File(in));

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




    

    private static void test() {
/*
        int[] a = new int[]{ 32, 31, 34, 12, 0, -1 ,12 , 9};

        System.out.print("Исходный Массив: ");
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i]+" ");
        }
        
        int minIndex;
        for (int i = 0; i < a.length - 1; i++ ) {
            minIndex = i;

            for (int j = i + 1; j < a.length; j++) {
                if(a[j] < a[minIndex]){
                    minIndex = j;
                }
            }

            int temp = a[i];
            a[i] = a[minIndex];
            a[minIndex] = temp;
        }
        
        System.out.print("\nОтсортированный Массив: ");
        for (int i = 0; i < a.length; i++) {
            System.out.print(a[i]+" ");
        }
        System.out.println();
        System.exit(0);*/
    }
}
