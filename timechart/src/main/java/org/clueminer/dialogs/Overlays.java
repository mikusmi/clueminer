package org.clueminer.dialogs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import org.clueminer.chart.api.Overlay;
import org.clueminer.chart.ChartDataImpl;
import org.clueminer.chart.ChartFrame;
import org.clueminer.chart.factory.OverlayFactory;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.nodes.Node;
import org.openide.windows.WindowManager;

/**
 *
 * @author Tomas Barton
 */
public class Overlays extends javax.swing.JDialog {

    private static final long serialVersionUID = 1895463427466642217L;
    private ChartFrame parent;
    private List<Overlay> initial;
    private List<Overlay> selected;
    private List<Overlay> unselected;

    /**
     * Creates new form Overlays
     *
     * @param parent
     * @param modal
     */
    public Overlays(Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        parent.setIconImage(WindowManager.getDefault().getMainWindow().getIconImage());
    }

    public void setChartFrame(ChartFrame cf) {
        parent = cf;
    }

    public void initForm() {
        btnAdd.setEnabled(false);
        btnRemove.setEnabled(false);

        selected = new ArrayList<Overlay>();
        unselected = OverlayFactory.getInstance().getAll();

        selected = parent.getSplitPanel().getChartPanel().getOverlays();
        initial = selected;

        scrollPane.setEnabled(false);
        scrollPane.setLayout(new BorderLayout());
        //    scrollPane.setPreferredSize(new Dimension(549, 346));
        //  scrollPane.setMinimumSize(new Dimension(250, 250));

        lstSelected.setListData(getArray(selected, true));
        lstUnselected.setListData(getArray(unselected, false));
    }

    private void setPanel(Overlay o) {
        PropertySheet prop = new PropertySheet();
        prop.setNodes(new Node[]{
            o.getNode()
        });
        Dimension d = prop.getSize();
        prop.setPreferredSize(new Dimension(549, 150));
        scrollPane.setEnabled(true);
        scrollPane.removeAll();
        scrollPane.add(prop, BorderLayout.CENTER);
        scrollPane.validate();
        validate();
        repaint();
    }

    private String[] getArray(List<Overlay> list, boolean label) {
        String[] array = new String[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = label == true ? list.get(i).getLabel() : list.get(i).getName();
        }
        return array;
    }

    public @Override
    void paint(Graphics g) {
        super.paint(g);
        int index = lstSelected.getSelectedIndex();
        lstSelected.setListData(getArray(selected, true));
        lstSelected.setSelectedIndex(index);
    }

    public @Override
    void update(Graphics g) {
        super.update(g);
        repaint();
    }

    public @Override
    void setVisible(boolean b) {
        super.setVisible(b);
        if (!b) {
            parent.componentFocused();
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPane1 = new javax.swing.JPanel();
        lbOverlays = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        lstUnselected = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        lstSelected = new javax.swing.JList();
        btnAdd = new javax.swing.JButton();
        btnRemove = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();
        lbProperities = new javax.swing.JLabel();
        scrollPane = new javax.swing.JPanel();
        btnApply = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnOk = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPane1.setPreferredSize(new java.awt.Dimension(702, 308));

        lbOverlays.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.lbOverlays.text")); // NOI18N

        lstUnselected.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstUnselected.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstUnselectedMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(lstUnselected);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.jLabel1.text")); // NOI18N

        lstSelected.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        lstSelected.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lstSelectedMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(lstSelected);

        btnAdd.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.btnAdd.text")); // NOI18N
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnRemove.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.btnRemove.text")); // NOI18N
        btnRemove.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRemoveActionPerformed(evt);
            }
        });

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);

        lbProperities.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.lbProperities.text")); // NOI18N

        scrollPane.setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout scrollPaneLayout = new javax.swing.GroupLayout(scrollPane);
        scrollPane.setLayout(scrollPaneLayout);
        scrollPaneLayout.setHorizontalGroup(
            scrollPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
        );
        scrollPaneLayout.setVerticalGroup(
            scrollPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );

        btnApply.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.btnApply.text")); // NOI18N
        btnApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApplyActionPerformed(evt);
            }
        });

        btnCancel.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.btnCancel.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnOk.setText(org.openide.util.NbBundle.getMessage(Overlays.class, "Overlays.btnOk.text")); // NOI18N
        btnOk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnOkActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPane1Layout = new javax.swing.GroupLayout(jPane1);
        jPane1.setLayout(jPane1Layout);
        jPane1Layout.setHorizontalGroup(
            jPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)
                        .addComponent(lbOverlays, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                    .addGroup(jPane1Layout.createSequentialGroup()
                        .addComponent(btnAdd)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(btnRemove))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPane1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lbProperities)
                        .addGap(399, 399, 399))
                    .addGroup(jPane1Layout.createSequentialGroup()
                        .addGroup(jPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPane1Layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPane1Layout.createSequentialGroup()
                                .addGap(99, 99, 99)
                                .addComponent(btnOk)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnApply)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnCancel)))
                        .addContainerGap())))
        );
        jPane1Layout.setVerticalGroup(
            jPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPane1Layout.createSequentialGroup()
                        .addComponent(lbProperities)
                        .addGap(3, 3, 3)
                        .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(btnApply)
                            .addComponent(btnOk)))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPane1Layout.createSequentialGroup()
                        .addGroup(jPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jSeparator2, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPane1Layout.createSequentialGroup()
                                .addComponent(lbOverlays)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnAdd, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnRemove, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 558, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 298, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        int i = lstUnselected.getSelectedIndex();
        if (i != -1) {
            Overlay o = unselected.get(i).newInstance();
            selected.add(o);
            lstSelected.setListData(getArray(selected, true));
            setPanel(o);
        }
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnOkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnOkActionPerformed
        parent.getSplitPanel().getChartPanel().clearOverlays();
        ChartDataImpl cd = (ChartDataImpl) parent.getChartData();
        cd.removeAllOverlaysDatasetListeners();
        if (selected.size() > 0) {
            for (int i = 0; i < selected.size(); i++) {
                Overlay o = selected.get(i);
                o.setLogarithmic(parent.getChartProperties().getYAxis().isLogarithmic());
//                o.setDataset(parent.getChartData().getDataset(false));
                o.calculate();
                parent.getSplitPanel().getChartPanel().addOverlay(o);
            }
        }
        parent.revalidate();
        parent.repaint();
        setVisible(false);
    }//GEN-LAST:event_btnOkActionPerformed

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelActionPerformed
        selected = initial;
        setVisible(false);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void btnApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApplyActionPerformed
        parent.getSplitPanel().getChartPanel().clearOverlays();
        ChartDataImpl cd = (ChartDataImpl) parent.getChartData();
        cd.removeAllOverlaysDatasetListeners();
        if (selected.size() > 0) {
            for (int i = 0; i < selected.size(); i++) {
                Overlay o = selected.get(i);
                o.setLogarithmic(parent.getChartProperties().getYAxis().isLogarithmic());
                //               o.setDataset(parent.getChartData().getDataset(false));
                o.calculate();
                parent.getSplitPanel().getChartPanel().addOverlay(o);
            }
        }
        parent.revalidate();
        parent.repaint();
    }//GEN-LAST:event_btnApplyActionPerformed

    private void btnRemoveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRemoveActionPerformed
        int i = lstSelected.getSelectedIndex();
        if (i != -1) {
            selected.remove(i);
            if (selected.isEmpty()) {
                btnRemove.setEnabled(false);
            }
            scrollPane.setEnabled(false);
            lstSelected.setListData(getArray(selected, true));
            validate();
            repaint();
        }
    }//GEN-LAST:event_btnRemoveActionPerformed

    private void lstUnselectedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstUnselectedMouseClicked
        switch (evt.getClickCount()) {
            case 1:
                scrollPane.setEnabled(false);
                btnAdd.setEnabled(true);
                btnRemove.setEnabled(false);
                break;
            case 2:
                btnAdd.doClick();
                break;
        }
    }//GEN-LAST:event_lstUnselectedMouseClicked

    private void lstSelectedMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lstSelectedMouseClicked
        switch (evt.getClickCount()) {
            case 1:
                btnAdd.setEnabled(false);
                if (selected.size() > 0) {
                    btnRemove.setEnabled(true);
                } else {
                    btnRemove.setEnabled(false);
                }
                int i = lstSelected.getSelectedIndex();
                if (i != -1) {
                    setPanel(selected.get(i));
                }
                break;
            case 2:
                btnRemove.doClick();
                break;
        }
    }//GEN-LAST:event_lstSelectedMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                Overlays dialog = new Overlays(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnApply;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnRemove;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPane1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JLabel lbOverlays;
    private javax.swing.JLabel lbProperities;
    private javax.swing.JList lstSelected;
    private javax.swing.JList lstUnselected;
    private javax.swing.JPanel scrollPane;
    // End of variables declaration//GEN-END:variables
}
