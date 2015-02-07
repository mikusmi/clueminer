package org.clueminer.meta.view;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.SortedList;
import ca.odell.glazedlists.swing.DefaultEventTableModel;
import ca.odell.glazedlists.swing.TableComparatorChooser;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import org.clueminer.clustering.api.ClusterEvaluation;
import org.clueminer.clustering.api.factory.EvaluationFactory;
import org.clueminer.dataset.api.Dataset;
import org.clueminer.dataset.api.Instance;
import org.clueminer.meta.api.MetaResult;
import org.clueminer.meta.api.MetaStorage;
import org.clueminer.project.api.ProjectController;
import org.openide.util.Lookup;

/**
 *
 * @author Tomas Barton
 */
class MetaPanel extends JPanel {

    private static final long serialVersionUID = 6800384383501394578L;
    private JTable instaceJTable;
    private JScrollPane instanceListScrollPane;
    private final EventList<String[]> resultsList;
    private Dataset<? extends Instance> dataset;
    private MetaStorage storage;
    private JComboBox<String> evolutions;
    private JComboBox<String> evaluators;

    public MetaPanel() {
        this.resultsList = new BasicEventList<>();
        initialize();
    }

    private void initialize() {
        setLayout(new GridBagLayout());
        evolutions = new JComboBox<>();
        evolutions.addActionListener(new QueryReloader());
        evaluators = new JComboBox<>(initEvaluators());
        evaluators.addActionListener(new QueryReloader());

        // lock while creating the transformed models
        resultsList.getReadWriteLock().readLock().lock();
        try {
            SortedList<String[]> sortedItems = new SortedList<>(resultsList, new ElementComparator());

            //FilterList<String[]> textFilteredIssues = new FilterList<>(propertieList, new TextComponentMatcherEditor<>(filterEdit, new StringTextFilterator()));
            DefaultEventTableModel<String[]> infoListModel = new DefaultEventTableModel<>(sortedItems, new MetaTableFormat());
            instaceJTable = new JTable(infoListModel);
            TableComparatorChooser tableSorter = TableComparatorChooser.install(instaceJTable, sortedItems, TableComparatorChooser.MULTIPLE_COLUMN_MOUSE);
        } finally {
            resultsList.getReadWriteLock().readLock().unlock();
        }

        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.CENTER;
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 0.0;
        c.weighty = 0.0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.fill = GridBagConstraints.BOTH;

        add(new JLabel("Source: "), c);
        c.gridy = 1;
        add(new JLabel("Evaluation: "), c);
        c.gridy = 0;
        c.insets = new Insets(5, 55, 5, 5);
        c.weightx = 0.15;
        add(evolutions, c);
        c.gridy = 1;
        c.insets = new Insets(5, 70, 5, 5);
        add(evaluators, c);
        instanceListScrollPane = new JScrollPane(instaceJTable);
        add(instanceListScrollPane, new GridBagConstraints(0, 2, 1, 4, 0.85, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 5, 5, 5), 0, 0));
    }

    private String[] initEvaluators() {
        EvaluationFactory ef = EvaluationFactory.getInstance();
        List<String> list = ef.getProviders();
        return list.toArray(new String[list.size()]);
    }

    public void updateDataset(Dataset<? extends Instance> d) {
        this.dataset = d;
    }

    protected String currentEvolution() {
        return (String) evolutions.getSelectedItem();
    }

    protected ClusterEvaluation currentEvaluator() {
        EvaluationFactory ef = EvaluationFactory.getInstance();
        return ef.getProvider((String) evaluators.getSelectedItem());
    }

    protected void updateResult() {
        if (storage != null) {
            String evo = currentEvolution();
            ClusterEvaluation eval = currentEvaluator();
            if (evo != null && eval != null && getDataset() != null) {
                Collection<MetaResult> col = storage.findResults(getDataset(), evo, eval);
                updateData(col);
            }
        }
    }

    private Dataset<? extends Instance> getDataset() {
        if (dataset == null) {
            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            dataset = pc.getCurrentWorkspace().getLookup().lookup(Dataset.class);
        }
        return dataset;
    }

    /**
     * Removes all items from the browser
     */
    public void clear() {
        resultsList.clear();
    }

    public void updateData(Collection<MetaResult> col) {
        for (MetaResult res : col) {
            resultsList.add(new String[]{String.valueOf(res.getK()), String.valueOf(res.getScore()), res.getTemplate()});
        }
    }

    public void setStorage(MetaStorage storage) {
        this.storage = storage;
        if (storage != null) {
            Collection<String> algs = storage.getEvolutionaryAlgorithms();
            if (algs.size() > 0) {
                evolutions.setModel(new DefaultComboBoxModel<>(algs.toArray(new String[algs.size()])));
            }
        }
    }

    private class QueryReloader implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            updateResult();
        }
    }

}
