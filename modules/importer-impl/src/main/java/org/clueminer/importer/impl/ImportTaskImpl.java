/*
 * Copyright (C) 2011-2018 clueminer.org
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.clueminer.importer.impl;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.swing.event.EventListenerList;
import org.clueminer.gui.msg.NotifyUtil;
import org.clueminer.importer.ImportController;
import org.clueminer.importer.ImportTask;
import org.clueminer.importer.gui.ReportPanel;
import org.clueminer.io.importer.api.Container;
import org.clueminer.processor.spi.Processor;
import org.clueminer.processor.spi.ProcessorUI;
import org.clueminer.project.api.ProjectController;
import org.clueminer.project.api.ProjectControllerUI;
import org.clueminer.project.api.Workspace;
import org.clueminer.spi.FileImporter;
import org.clueminer.spi.ImportListener;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Tomas Barton
 */
public class ImportTaskImpl implements ImportTask {

    private final FileImporter importer;
    private final FileObject fileObject;
    private final ImportController controller;
    private final transient EventListenerList importListeners = new EventListenerList();
    private final static Logger LOG = LoggerFactory.getLogger(ImportTaskImpl.class);
    private int num;

    public ImportTaskImpl(FileImporter importer, FileObject fileObject, ImportController controller, int num) {
        this.importer = importer;
        this.fileObject = fileObject;
        this.controller = controller;
        this.num = num;
    }

    @Override
    public Container getContainer() {
        return importer.getContainer();
    }

    @Override
    public void run() {
        InputStream stream = null;
        byte[] digest = null;
        try {
            stream = fileObject.getInputStream();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        final String containerSource;
        if (fileObject != null) {
            containerSource = fileObject.getPath();
        } else {
            containerSource = "missing source!";
        }

        try {
            LOG.info("preloading file: {}", containerSource);

            MessageDigest md = MessageDigest.getInstance("MD5");
            DigestInputStream dis = new DigestInputStream(stream, md);
            Container container = controller.importFile(fileObject, dis, importer, false);
            if (container != null) {
                container.setSource(containerSource);

                if (digest == null) {
                    digest = md.digest();
                    container.setMD5(convertMD5(digest));
                }
            }

            LOG.info("[" + num + "] file MD5: {} ", convertMD5(digest));
            LOG.info("showing import dialog...");
            //display import window
            finishImport(container);
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        } finally {
            if (fileObject.getPath().startsWith(System.getProperty("java.io.tmpdir"))) {
                try {
                    fileObject.delete();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        LOG.info("[{}] importer finished", num);
    }

    private String convertMD5(byte[] md5) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < md5.length; i++) {
            if ((0xff & md5[i]) < 0x10) {
                sb.append('0');
            }
            sb.append(Integer.toHexString(0xff & md5[i]));
        }
        return sb.toString();
    }

    private void finishImport(Container container) {
        if (container.verify()) {
            //Report panel
            ReportPanel reportPanel = new ReportPanel();
            //addListener(reportPanel);
            reportPanel.setCurrentFile(fileObject);
            reportPanel.setData(container);
            reportPanel.fileImporterChanged(importer);
            DialogDescriptor dd = new DialogDescriptor(reportPanel, NbBundle.getMessage(ReportPanel.class, "ReportPanel.title"));
            if (!DialogDisplayer.getDefault().notify(dd).equals(NotifyDescriptor.OK_OPTION)) {
                reportPanel.destroy();
                return;
            }
            //reportPanel.destroy();!!?

            if (dd.getValue() == DialogDescriptor.OK_OPTION) {
                //ok button was pressed
                final Processor processor = reportPanel.getProcessor();

                //Project
                Workspace workspace = null;
                ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
                ProjectControllerUI pcui = Lookup.getDefault().lookup(ProjectControllerUI.class);
                if (pc.getCurrentProject() == null) {
                    pcui.newProject();
                    workspace = pc.getCurrentWorkspace();
                }
                LOG.info("processing input file");
                //actual data import
                controller.process(container, processor, workspace);
                fireDataLoaded();
            } else {
                //cancel button was pressed
            }

        } else {
            NotifyUtil.error("Error", "Bad container", false);
        }
    }

    private ProcessorUI getProcessorUI(Processor processor) {
        for (ProcessorUI pui : Lookup.getDefault().lookupAll(ProcessorUI.class)) {
            if (pui.isUIFoProcessor(processor)) {
                return pui;
            }
        }
        return null;
    }

    @Override
    public void addListener(ImportListener listener) {
        importListeners.add(ImportListener.class, listener);
    }

    @Override
    public void removeListener(ImportListener listener) {
        importListeners.remove(ImportListener.class, listener);
    }

    public void fireDataLoaded() {
        for (ImportListener im : importListeners.getListeners(ImportListener.class)) {
            im.dataLoaded();
        }
    }
}
