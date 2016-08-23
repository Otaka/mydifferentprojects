package com.settings.editor.components;

import com.gooddies.events.ValueChangedEvent;
import com.gooddies.swing.hFileChooser;
import com.gooddies.texteditors.DefaultTextField;
import com.settings.editor.builder.PropertyHolder;
import com.settings.editor.components.annotations.PropertyFile;
import com.settings.editor.components.utils.ContextMouseListener;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

/**
 * @author sad
 */
public class PropertyFileBuilder extends AbstractComponentPropertyBuilder {

    @Override
    public JComponent createComponent(final PropertyHolder property, final JPanel container) {
        JLabel label = new JLabel(property.getName());
        final DefaultTextField pathField = new DefaultTextField();
        pathField.addMouseListener(new ContextMouseListener());
        pathField.setHelpMessage("adasd");
        final PropertyFile annotation = (PropertyFile) property.getAnnotation();

        File file = (File) property.getValue();

        pathField.setTextAndScrollToStart(file == null ? "" : file.getAbsolutePath());

        pathField.setDropTarget(new DropTarget() {
            @Override
            public synchronized void drop(DropTargetDropEvent evt) {
                try {
                    evt.acceptDrop(DnDConstants.ACTION_COPY);
                    List<File> droppedFiles = (List<File>) evt.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
                    if (!droppedFiles.isEmpty()) {
                        File file = droppedFiles.get(0);
                        pathField.setTextAndScrollToStart(file.getAbsolutePath());
                        property.setValue(file);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        container.add(label, "wrap");
        JButton selectButton = new JButton("Choose");
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new MigLayout("insets 0 0 0 0"));
        wrapperPanel.add(pathField, "width 100%");
        wrapperPanel.add(selectButton, "width 25px");
        container.add(wrapperPanel, "width " + annotation.width() + ", height " + annotation.height() + ", wrap");
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                hFileChooser chooser = new hFileChooser();
                if (annotation.selectDir() == false) {
                    chooser.setFileFilter(annotation.filter());
                } else {
                    chooser.setFileSelectionMode(hFileChooser.DIRECTORIES_ONLY);
                }
                File oldFile = (File) property.getValue();
                if (oldFile != null) {
                    chooser.setSelectedFile(oldFile);
                }

                int result = chooser.showDialog(container, "Choose");
                if (result == hFileChooser.APPROVE_OPTION) {
                    File file = chooser.getSelectedFile();
                    pathField.setTextAndScrollToStart(file.getAbsolutePath());
                    property.setValue(file);
                }

            }
        });
        pathField.setValueChangedEvent(new ValueChangedEvent<String>() {
            @Override
            protected void valueChanged(String value) {
                property.setValue(new File(pathField.getText()));
            }
        });
        return wrapperPanel;
    }

}
