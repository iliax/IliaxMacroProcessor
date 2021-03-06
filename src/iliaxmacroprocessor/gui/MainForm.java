/*
 * MainForm.java
 *
 * Created on 08.04.2012, 14:13:50
 */
package iliaxmacroprocessor.gui;

import iliaxmacroprocessor.logging.ConsoleAppenderImpl;
import iliaxmacroprocessor.logic.MacroProcessor;
import iliaxmacroprocessor.logic.Macros;
import iliaxmacroprocessor.logic.TextDataHolder;
import java.awt.FileDialog;
import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.CloseAction;
import javax.swing.plaf.basic.BasicTextUI.BasicCaret;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

/**
 *
 * @author iliax
 */
public class MainForm extends javax.swing.JFrame {
    
    private static final String LS = System.getProperty("line.separator");

    private static final Logger LOG = Logger.getLogger(MainForm.class.getName());

    private GuiConfig _guiConfig;
    private TextDataHolder _textDataHolder;

    volatile AtomicBoolean atomicBoolean = new AtomicBoolean(false);

    private Thread _workingThread = null;

    private volatile MacroProcessor macroProcessor;
    /** Creates new form MainForm */
    public MainForm() {

        initComponents();

        _nextStepButton.setEnabled(false);
        _start2ndScanButt.setVisible(false);
        _endButton.setEnabled(false);

        _logField.setEditable(false);

        ConsoleAppenderImpl.addListener(new ConsoleAppenderImpl.LogEventAppendable() {
            @Override
            public void append(LoggingEvent aLogEvent) {
                _logField.setText(_logField.getText() + aLogEvent.getMessage() + LS);
                _logField.setCaretPosition(_logField.getText().length());
            }
        });

        _guiConfig = new GuiConfig(_sourseTextField, _outTextField, _start2ndScanButt,
                _nextStepButton, _endButton, _macrosesList);

        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        _sourseTextField = new javax.swing.JTextPane();
        jScrollPane2 = new javax.swing.JScrollPane();
        _outTextField = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        _start1stScanButt = new javax.swing.JButton();
        _nextStepButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        _macrosesList = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        _macrosStrings = new javax.swing.JTextArea();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        _logField = new javax.swing.JTextPane();
        jLabel5 = new javax.swing.JLabel();
        _endButton = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        _start2ndScanButt = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        _openMenuItem = new javax.swing.JMenuItem();
        _saveMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("iliaxMacroProcessor GUI");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        _sourseTextField.setFont(new java.awt.Font("Monospaced", 0, 12));
        _sourseTextField.addInputMethodListener(new java.awt.event.InputMethodListener() {
            public void caretPositionChanged(java.awt.event.InputMethodEvent evt) {
                _sourseTextFieldCaretPositionChanged(evt);
            }
            public void inputMethodTextChanged(java.awt.event.InputMethodEvent evt) {
                _sourseTextFieldInputMethodTextChanged(evt);
            }
        });
        _sourseTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                _sourseTextFieldKeyTyped(evt);
            }
        });
        jScrollPane1.setViewportView(_sourseTextField);

        _outTextField.setEditable(false);
        _outTextField.setFont(new java.awt.Font("Monospaced", 0, 12));
        jScrollPane2.setViewportView(_outTextField);

        jLabel1.setText("IN:");

        jLabel2.setText("OUT:");

        _start1stScanButt.setText("начать первый проход");
        _start1stScanButt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _start1stScanButtActionPerformed(evt);
            }
        });

        _nextStepButton.setText("Следующий шаг");
        _nextStepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _nextStepButtonActionPerformed(evt);
            }
        });

        _macrosesList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                _macrosesListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(_macrosesList);

        jLabel3.setText("Macroses:");

        _macrosStrings.setColumns(20);
        _macrosStrings.setEditable(false);
        _macrosStrings.setRows(5);
        jScrollPane4.setViewportView(_macrosStrings);

        jLabel4.setText("Macros data:");

        _logField.setEditable(false);
        jScrollPane5.setViewportView(_logField);

        jLabel5.setText("LOG:");

        _endButton.setText("До конца прохода");
        _endButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _endButtonActionPerformed(evt);
            }
        });

        jButton1.setText("Clear LOG");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        _start2ndScanButt.setText("SECOND SCAN");

        jMenu1.setText("File");

        _openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        _openMenuItem.setText("Open");
        _openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _openMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(_openMenuItem);

        _saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        _saveMenuItem.setText("Save");
        _saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                _saveMenuItemActionPerformed(evt);
            }
        });
        jMenu1.add(_saveMenuItem);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 330, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(_start1stScanButt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(_nextStepButton)))
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                            .addComponent(jLabel4)
                            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 328, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(_endButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(_start2ndScanButt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 259, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 738, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(_start1stScanButt)
                    .addComponent(_nextStepButton)
                    .addComponent(jButton1)
                    .addComponent(_start2ndScanButt)
                    .addComponent(_endButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void _openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__openMenuItemActionPerformed

        JFileChooser fileChooser = new JFileChooser(new File("."));
        fileChooser.setFileFilter(QBIX_FILE_FILTER);
        
        int opt = fileChooser.showOpenDialog(this);
        if (opt == JFileChooser.CANCEL_OPTION) {
            return;
        }

        File file = fileChooser.getSelectedFile();

        _textDataHolder = new TextDataHolder(file);

        _sourseTextField.setText(_textDataHolder.getText());
    }//GEN-LAST:event__openMenuItemActionPerformed

    private void _saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__saveMenuItemActionPerformed
        if (_textDataHolder != null) {
            try {
                _textDataHolder.synchTextWithFile(_textDataHolder.file, _sourseTextField.getText());

                JOptionPane.showMessageDialog(this, "file was updated!");

            } catch (RuntimeException re) {
                LOG.error("io error", re);
            }
        } else {
            JOptionPane.showMessageDialog(this, "no file to save");
        }
    }//GEN-LAST:event__saveMenuItemActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        
    }//GEN-LAST:event_formWindowClosed

    private void _start1stScanButtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__start1stScanButtActionPerformed
        if(_textDataHolder == null){
            JOptionPane.showMessageDialog(this, "no data to analyse");
            return;
        }

        if(_workingThread != null && _workingThread.isAlive()){
            try {
                _workingThread.stop();
            } catch(Exception e){}
        }

        _textDataHolder.synchTextWithFile(_textDataHolder.file, _sourseTextField.getText());

        _logField.setText("");
        atomicBoolean.set(false);

        _outTextField.setText("");
        _macrosStrings.setText("");

        macroProcessor = new MacroProcessor(_guiConfig, _textDataHolder.getStrings(TextDataHolder.NO_EMPTY_STRINGS), atomicBoolean);
        //macroProcessor.start1stScan();
        
        _workingThread = new Thread(){

            @Override
            public void run() {
                try {
                    macroProcessor.start1stScan();
                } catch(IndexOutOfBoundsException ioobe){
                    LOG.info("ОШИБКА! НЕ ЗАКРЫТЫЙ БЛОК!");
                } catch(Exception e){
                    LOG.info("ОШИБКА! "+e.getMessage());
                }
            }
        };
        
        _workingThread.start();

        _nextStepButton.setEnabled(true);
        _endButton.setEnabled(true);

    }//GEN-LAST:event__start1stScanButtActionPerformed

    private void _nextStepButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__nextStepButtonActionPerformed
    
        atomicBoolean.set(false);

        synchronized(MacroProcessor.class){
            MacroProcessor.class.notifyAll();
        }
    }//GEN-LAST:event__nextStepButtonActionPerformed

    private void _endButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event__endButtonActionPerformed
    if(_workingThread != null){
            if(atomicBoolean.get() == false){
                atomicBoolean.set(true);
                 synchronized(MacroProcessor.class){
                    MacroProcessor.class.notifyAll();
                }
            }
        }
    }//GEN-LAST:event__endButtonActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        _logField.setText("");
    }//GEN-LAST:event_jButton1ActionPerformed

    private void _sourseTextFieldInputMethodTextChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event__sourseTextFieldInputMethodTextChanged

    }//GEN-LAST:event__sourseTextFieldInputMethodTextChanged

    private void _sourseTextFieldCaretPositionChanged(java.awt.event.InputMethodEvent evt) {//GEN-FIRST:event__sourseTextFieldCaretPositionChanged
        
    }//GEN-LAST:event__sourseTextFieldCaretPositionChanged

    private void _sourseTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event__sourseTextFieldKeyTyped
        
    }//GEN-LAST:event__sourseTextFieldKeyTyped

    private void _macrosesListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event__macrosesListValueChanged
        if(macroProcessor != null){

            if(_macrosesList.getSelectedValue() != null && _macrosesList.getSelectedIndex() != -1){
                String mName = _macrosesList.getSelectedValue().toString();
                for(Macros m: macroProcessor._macroses){
                    if(mName.equals(m.getName())){
                        _macrosStrings.setText(m.toString());
                    }
                }
            }
        }
    }//GEN-LAST:event__macrosesListValueChanged

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new MainForm().setVisible(true);
            }
        });
    }

    static final FileFilter QBIX_FILE_FILTER = new FileFilter() {

            @Override
            public boolean accept(File f) {
                if (f.getName().endsWith(".qtr") || f.isDirectory()) {
                    return true;
                } else {
                    return false;
                }
            }

            @Override
            public String getDescription() {
                return "qbix translation file (.qtr)";
            }
        };
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton _endButton;
    private javax.swing.JTextPane _logField;
    private javax.swing.JTextArea _macrosStrings;
    private javax.swing.JList _macrosesList;
    private javax.swing.JButton _nextStepButton;
    private javax.swing.JMenuItem _openMenuItem;
    private javax.swing.JTextPane _outTextField;
    private javax.swing.JMenuItem _saveMenuItem;
    private javax.swing.JTextPane _sourseTextField;
    private javax.swing.JButton _start1stScanButt;
    private javax.swing.JLabel _start2ndScanButt;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    // End of variables declaration//GEN-END:variables
}
