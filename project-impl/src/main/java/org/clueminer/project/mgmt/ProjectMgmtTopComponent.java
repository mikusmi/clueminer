package org.clueminer.project.mgmt;

import java.awt.BorderLayout;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.view.BeanTreeView;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.clueminer.project.mgmt//ProjectMgmt//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "ProjectMgmtTopComponent",
        iconBase = "org/clueminer/project/impl/mgmt_16.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "org.clueminer.project.mgmt.ProjectMgmtTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_ProjectMgmtAction",
        preferredID = "ProjectMgmtTopComponent"
)
@Messages({
    "CTL_ProjectMgmtAction=ProjectMgmt",
    "CTL_ProjectMgmtTopComponent=ProjectMgmt Window",
    "HINT_ProjectMgmtTopComponent=This is a ProjectMgmt window"
})
public final class ProjectMgmtTopComponent extends TopComponent implements LookupListener {

    private final BeanTreeView treeView;

    public ProjectMgmtTopComponent() {
        initComponents();
        setName(Bundle.CTL_ProjectMgmtTopComponent());
        setToolTipText(Bundle.HINT_ProjectMgmtTopComponent());

        setLayout(new BorderLayout());
        this.treeView = new BeanTreeView();
        //treeView.setRootVisible(false);
        add(treeView, BorderLayout.CENTER);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public void resultChanged(LookupEvent le) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
