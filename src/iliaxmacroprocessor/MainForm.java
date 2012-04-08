/*
 * MainForm.java
 *
 * Created on 08.04.2012, 14:13:50
 */
package iliaxmacroprocessor;

import java.awt.FileDialog;
import java.io.File;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicInternalFrameTitlePane.CloseAction;
import org.apache.log4j.Logger;

/**
 *
 * @author iliax
 */
public class MainForm extends javax.swing.JFrame {

    private static final Logger LOG = Logger.getLogger(MainForm.class.getName());

    private GuiConfig _guiConfig;
    private TextDataHolder _textDataHolder;

    /** Creates new form MainForm */
    public MainForm() {

        initComponents();

        _guiConfig = new GuiConfig(_sourseTextField);
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
        jTextPane1 = new javax.swing.JTextPane();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        _openMenuItem = new javax.swing.JMenuItem();
        _saveMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jScrollPane1.setViewportView(_sourseTextField);

        jScrollPane2.setViewportView(jTextPane1);

        jLabel1.setText("IN:");

        jLabel2.setText("OUT:");

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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addContainerGap(311, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                        .addGap(12, 12, 12))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(76, Short.MAX_VALUE))
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
            //TODO
            JOptionPane.showMessageDialog(this, "no file to save");
        }
    }//GEN-LAST:event__saveMenuItemActionPerformed

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
    private javax.swing.JMenuItem _openMenuItem;
    private javax.swing.JMenuItem _saveMenuItem;
    private javax.swing.JTextPane _sourseTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextPane jTextPane1;
    // End of variables declaration//GEN-END:variables
}
