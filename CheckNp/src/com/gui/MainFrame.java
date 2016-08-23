package com.gui;

import com.gooddies.swing.Toast;
import com.gooddies.swing.hComboBox;
import com.gui.instruments.*;
import com.n.NProcessor;
import com.swingson.SwingsonGuiBuilder;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;

public class MainFrame extends JFrame {
    private final RenderPane renderPane = new RenderPane();
    private final JToggleButton moveInstrumentButton = new JToggleButton("Двигать");
    private final JToggleButton activationInstrumentButton = new JToggleButton("Активация");
    private final JToggleButton connectInstrumentButton = new JToggleButton("Соединить");
    private final JToggleButton addNewNodeInstrumentButton = new JToggleButton("Новый");
    private final ToggleButtonGroup group = new ToggleButtonGroup();
    private final JButton saveButton = new JButton();
    private final JButton loadButton = new JButton();
    private final hComboBox<String> fileSwitcher = new hComboBox<>();
    private final NProcessor processor = new NProcessor();

    public MainFrame() throws HeadlessException {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Главное окно");
        SwingsonGuiBuilder.createGuiFromJsonInPackage(this);
        group.add(moveInstrumentButton).add(activationInstrumentButton).add(connectInstrumentButton).add(addNewNodeInstrumentButton);
        moveInstrumentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renderPane.setSelectedInstrument(new MoveInstrument(renderPane));
            }
        });
        activationInstrumentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renderPane.setSelectedInstrument(new ActivationInstrument(renderPane));
            }
        });
        connectInstrumentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renderPane.setSelectedInstrument(new ConnectInstrument(renderPane));
            }
        });
        addNewNodeInstrumentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                renderPane.setSelectedInstrument(new NewNodeInstrument(renderPane));
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (getSelectedFileName() != null) {
                        renderPane.save(renderPane.getNodes(), new File("./" + getSelectedFileName()));
                        saveFileToFileSwitcher();
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });

        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (getSelectedFileName() != null) {
                        renderPane.load(new File("./" + getSelectedFileName()));
                    }
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }
            }
        });
        initFileSwitcher();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {

            @Override
            public void uncaughtException(Thread t, Throwable e) {
                error(e.getMessage());
            }
        });
    }

    private void error(String error) {
        Toast.makeText(rootPane, error, 5000, Toast.Style.ERROR).display();
    }

    private String getSelectedFileName() {
        String selectedFileName = fileSwitcher.getSelectedItem();
        if (selectedFileName == null || selectedFileName.trim().isEmpty()) {
            error("Please enter file name");
            return null;
        }

        selectedFileName = selectedFileName.trim().toLowerCase();
        if (!selectedFileName.endsWith(".bin")) {
            selectedFileName = selectedFileName + ".bin";
        }
        return selectedFileName;
    }

    private void saveFileToFileSwitcher() {
        String selectedFileName = getSelectedFileName();
        if (!selectedFileName.equals(fileSwitcher.getSelectedItem())) {
            fileSwitcher.addItem(getSelectedFileName());
            fileSwitcher.setSelectedItem(getSelectedFileName());
        }
    }

    private void initFileSwitcher() {
        File[] files = new File("./").listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".bin");
            }
        });
        fileSwitcher.clearItems();
        for (File f : files) {
            fileSwitcher.addItem(f.getName());
        }
    }

    private void onProcess() {
        processor.process(renderPane.getNodes());
    }
}
