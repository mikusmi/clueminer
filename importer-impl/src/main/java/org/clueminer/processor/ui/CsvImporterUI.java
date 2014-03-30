package org.clueminer.processor.ui;

import javax.swing.JPanel;
import org.clueminer.importer.impl.CsvImporter;
import org.clueminer.spi.Importer;
import org.clueminer.spi.ImporterUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author deric
 */
@ServiceProvider(service = ImporterUI.class)
public class CsvImporterUI extends javax.swing.JPanel implements ImporterUI {

    private CsvImporter importer;

    /**
     * Creates new form CsvImporterUI
     */
    public CsvImporterUI() {
        initComponents();
    }

    @Override
    public void setup(Importer importer) {
        this.importer = (CsvImporter) importer;

    }

    @Override
    public JPanel getPanel() {
        return this;
    }

    @Override
    public void unsetup(boolean update) {
        //
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(getClass(), "CsvImporterUI.displayName");
    }

    @Override
    public boolean isUIForImporter(Importer importer) {
        return importer instanceof CsvImporter;
    }

    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lbSeparator = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        chckHeader = new javax.swing.JCheckBox();
        chckQuotation = new javax.swing.JCheckBox();
        lbNumType = new javax.swing.JLabel();
        cbDefaultType = new javax.swing.JComboBox();

        org.openide.awt.Mnemonics.setLocalizedText(lbSeparator, org.openide.util.NbBundle.getMessage(CsvImporterUI.class, "CsvImporterUI.lbSeparator.text")); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { ",", ";", "<tab>", "<space>", "|" }));

        org.openide.awt.Mnemonics.setLocalizedText(chckHeader, org.openide.util.NbBundle.getMessage(CsvImporterUI.class, "CsvImporterUI.chckHeader.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(chckQuotation, org.openide.util.NbBundle.getMessage(CsvImporterUI.class, "CsvImporterUI.chckQuotation.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(lbNumType, org.openide.util.NbBundle.getMessage(CsvImporterUI.class, "CsvImporterUI.lbNumType.text")); // NOI18N

        cbDefaultType.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "double", "float", "int", "long" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chckHeader)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbSeparator)
                        .addGap(27, 27, 27)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(chckQuotation)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lbNumType)
                        .addGap(3, 3, 3)
                        .addComponent(cbDefaultType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(142, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbSeparator)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbNumType)
                    .addComponent(cbDefaultType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(chckHeader)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chckQuotation)
                .addContainerGap(44, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cbDefaultType;
    private javax.swing.JCheckBox chckHeader;
    private javax.swing.JCheckBox chckQuotation;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel lbNumType;
    private javax.swing.JLabel lbSeparator;
    // End of variables declaration//GEN-END:variables

}
