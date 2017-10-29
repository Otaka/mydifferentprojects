package com.jogl.gui;

import com.gooddies.swing.hList;
import com.jogl.engine.node.Node;
import com.swingson.SwingsonGuiBuilder;
import com.swingson.properties.specificclass_impl.MouseActionPropertyProcessor;
import java.awt.HeadlessException;
import java.awt.Point;
import java.io.File;
import java.io.FileFilter;
import java.util.*;
import javax.swing.*;

/**
 * @author Dmitry
 */
public class ManipFrame extends JFrame {

    private final hList<File> fileList = new hList<>();
    private final JPanel contentPanel = new JPanel();
    private OnLoadFile onLoadFile;

    public ManipFrame() throws HeadlessException {
        SwingsonGuiBuilder.createGuiFromJsonInPackage(this);
        fileList.setItemTextExtractor(new hList.ItemTextExtractor<File>() {
            @Override
            public String getText(File text, int index) {
                return text.getName();
            }
        });

        loadFiles();
    }

    public void setOnLoadFile(OnLoadFile onLoadFile) {
        this.onLoadFile = onLoadFile;
    }

    public void fileListClicked(Point point, MouseActionPropertyProcessor.Button button, int modifier, int clickCount) {
        if (clickCount > 1) {
            File file = fileList.getSelectedItem();
            if (file == null) {
                return;
            }
            onLoadFile.load(file);
        }
    }

    private void loadFiles() {
        final File baseFolder = new File("g:\\kotor_Extracted\\anoter models\\");
        Thread thread = new Thread() {
            @Override
            public void run() {
                final List<File> files = new ArrayList<>();
                File[] folders = baseFolder.listFiles((FileFilter) new FileFilter() {
                    @Override
                    public boolean accept(File pathname) {
                        return pathname.isDirectory();
                    }
                });
                for (File dir : folders) {
                    File[] mdls = dir.listFiles(new FileFilter() {
                        @Override
                        public boolean accept(File pathname) {
                            return pathname.getName().endsWith(".mdl");
                        }
                    });
                    for (File mdl : mdls) {
                        files.add(mdl);
                    }
                }

                Collections.sort(files);
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        fileList.addItems(files);
                    }
                });
            }
        };

        thread.start();
    }

    public void init(Node node) {

    }
}
