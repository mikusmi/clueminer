package org.clueminer.infopanel;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.hts.api.HtsInstance;
import org.clueminer.hts.api.HtsPlate;
import org.clueminer.project.api.Project;
import org.clueminer.project.api.ProjectController;
import org.clueminer.project.api.Workspace;
import org.clueminer.project.api.WorkspaceListener;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(dtd = "-//org.clueminer.infopanel//InfoPanel//EN",
                     autostore = false)
@TopComponent.Description(preferredID = "InfoPanelTopComponent",
                          iconBase = "org/clueminer/infopanel/info16.png",
                          persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "properties", openAtStartup = true)
@ActionID(category = "Window", id = "org.clueminer.infopanel.InfoPanelTopComponent")
@ActionReference(path = "Menu/Window" /*
 * , position = 333
 */)
@TopComponent.OpenActionRegistration(displayName = "#CTL_InfoPanelAction",
                                     preferredID = "InfoPanelTopComponent")
@Messages({
    "CTL_InfoPanelAction=InfoPanel",
    "CTL_InfoPanelTopComponent=InfoPanel Window",
    "HINT_InfoPanelTopComponent=This is a InfoPanel window"
})
public final class InfoPanelTopComponent extends TopComponent implements LookupListener {

    private static final long serialVersionUID = 2614692318647805746L;
    private InfoTable table;
    private Lookup.Result<Dataset> result = null;
    private Lookup.Result<HtsPlate> htsResult = null;
    protected static Project project;
    private static final Logger logger = Logger.getLogger(InfoPanelTopComponent.class.getName());

    public InfoPanelTopComponent() {
        initComponents();
        setName(Bundle.CTL_InfoPanelTopComponent());
        setToolTipText(Bundle.HINT_InfoPanelTopComponent());
        table = new InfoTable();
        add(table, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.GridBagLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {

        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.addWorkspaceListener(new WorkspaceListener() {
            @Override
            public void initialize(Workspace workspace) {
                logger.log(Level.INFO, "panel initialized");
            }

            @Override
            public void select(Workspace workspace) {
                logger.log(Level.INFO, "workspace: {0}", workspace.toString());
                logger.log(Level.INFO, "infopanel selected");
                System.out.println("workspace selected: got result (plate)");
                htsResult = workspace.getLookup().lookupResult(HtsPlate.class);
                System.out.println("lookup res= " + htsResult.toString());

                HtsPlate plt = workspace.getLookup().lookup(HtsPlate.class);
                System.out.println("got plate, size: " + plt);
                //  result.addLookupListener(parent);

                Dataset<Instance> dataset = workspace.getLookup().lookup(Dataset.class);
                if (dataset != null) {
                    logger.log(Level.INFO, "got dataset {0}", dataset.size());
                    /* System.out.println("well map");
                     System.out.println("dataset size = " + dataset.size());
                     for (Instance inst : dataset) {
                     System.out.println("inst: " + inst.toString());
                     }*/
                }

            }

            @Override
            public void unselect(Workspace workspace) {
                if (result != null) {
                    //   result.removeLookupListener(parent);
                }
            }

            @Override
            public void close(Workspace workspace) {
            }

            @Override
            public void disable() {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void projectActivated(Project proj) {
                project = proj;
                projectChanged();
            }
        });

        result = Utilities.actionsGlobalContext().lookupResult(Dataset.class);
        result.addLookupListener(this);
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
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

    private void updateDataset(Dataset<Instance> d) {
        if (d != null) {
            logger.log(Level.INFO, "info panel: res change. dataset size{0}", d.size());
            int attrCnt = 2;
            String[][] data = new String[d.size()][attrCnt];
            int i = 0;
            String name, id;
            for (Instance inst : d) {
                name = inst.getName();
                id = inst.getId();
                if (name == null) {
                    name = "";
                }
                if (id == null) {
                    id = "";
                }
                data[i++] = new String[]{name, id};
            }
            table.clear();
            table.setData(data);
        }
    }

    private void updatePlate(HtsPlate<HtsInstance> d) {
        if (d != null) {
            int attrCnt = 3;
            String[][] data = new String[d.size()][attrCnt];
            int i = 0;
            logger.log(Level.INFO, "hts plate: {0}, {1}", new Object[]{d.getName(), d.getId()});
            for (HtsInstance inst : d) {
                data[i++] = new String[]{inst.getName(), String.valueOf(inst.getRow()), String.valueOf(inst.getColumn())};
            }
            table.clear();
            table.setData(data);
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {

        if (result != null) {
            Collection<? extends Dataset> allDatasets = result.allInstances();
            for (Dataset<Instance> d : allDatasets) {
                updateDataset(d);
            }
        }

        if (htsResult != null) {
            Collection<? extends HtsPlate> allPlates = htsResult.allInstances();
            for (HtsPlate<HtsInstance> d : allPlates) {
                updatePlate(d);
            }
        }

    }

    protected void projectChanged() {
        final HtsPlate plt = project.getLookup().lookup(HtsPlate.class);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateDataset(plt);
            }
        });

    }
}