package com.gooddies.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 * @author Dmitry
 */
public class JMultilineTable extends JTable {

    public JMultilineTable() {
    }

    @Override
    public void setModel(TableModel dataModel) {
        super.setModel(dataModel);
        TableColumnModel cmodel = getColumnModel();
        TableCellEditor editor = new TablePopupEditor();
        TextAreaRenderer renderer = new TextAreaRenderer();
        for (int i = 0; i < cmodel.getColumnCount(); i++) {
            cmodel.getColumn(i).setCellRenderer(renderer);
            cmodel.getColumn(i).setCellEditor(editor);
        }
    }

    public class TablePopupEditor extends DefaultCellEditor implements TableCellEditor {
        private final PopupDialog popup;
        private String currentText = "";
        private final JButton tEditorComponent;

        public TablePopupEditor() {
            super(new JTextField());

            setClickCountToStart(2);

            //  Use a JButton as the editor component
            tEditorComponent = new JButton();
            tEditorComponent.setBackground(Color.white);
            tEditorComponent.setBorderPainted(false);
            tEditorComponent.setContentAreaFilled(false);

            //  Set up the dialog where we do the actual editing
            popup = new PopupDialog();
        }

        @Override
        public Object getCellEditorValue() {
            return currentText;
        }

        @Override
        public Component getTableCellEditorComponent(
                JTable table, Object value, boolean isSelected, int row, int column) {

            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    System.out.println("run");
                    popup.setText(currentText);
                    Point p = tEditorComponent.getLocationOnScreen();
                    popup.setLocation(p.x, p.y + tEditorComponent.getSize().height);
                    popup.show();
                    fireEditingStopped();
                }
            });

            currentText = value.toString();
            tEditorComponent.setText(currentText);
            return tEditorComponent;
        }

        /*
         *   Simple dialog containing the actual editing component
         */
        class PopupDialog extends JDialog implements ActionListener {
            private final JTextArea textArea;

            public PopupDialog() {
                super((Frame) null, "Change Description", true);

                textArea = new JTextArea(5, 20);
                textArea.setLineWrap(true);
                textArea.setWrapStyleWord(true);
                JScrollPane scrollPane = new JScrollPane(textArea);
                getContentPane().add(scrollPane);

                JButton cancel = new JButton("Cancel");
                cancel.addActionListener(this);
                JButton ok = new JButton("Ok");
                ok.setPreferredSize(cancel.getPreferredSize());
                ok.addActionListener(this);

                JPanel buttons = new JPanel();
                buttons.add(ok);
                buttons.add(cancel);
                getContentPane().add(buttons, BorderLayout.SOUTH);
                pack();

                getRootPane().setDefaultButton(ok);
            }

            public void setText(String text) {
                textArea.setText(text);
            }

            /*
             *   Save the changed text before hiding the popup
             */
            @Override
            public void actionPerformed(ActionEvent e) {
                if ("Ok".equals(e.getActionCommand())) {
                    currentText = textArea.getText();
                }

                textArea.requestFocusInWindow();
                setVisible(false);
            }
        }
    }

    public static class TextAreaRenderer extends JTextArea implements TableCellRenderer {
        private final DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();

        // Column heights are placed in this Map 
        private final Map<JTable, Map<Object, Map<Object, Integer>>> tablecellSizes = new HashMap<JTable, Map<Object, Map<Object, Integer>>>();

        /**
         * Creates a text area renderer.
         */
        public TextAreaRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
        }

        /**
         * Returns the component used for drawing the cell. This method is used
         * to configure the renderer appropriately before drawing.
         *
         * @param table - JTable object
         * @param value - the value of the cell to be rendered.
         * @param isSelected - isSelected true if the cell is to be rendered
         * with the selection highlighted; otherwise false.
         * @param hasFocus - if true, render cell appropriately.
         * @param row - The row index of the cell being drawn.
         * @param column - The column index of the cell being drawn.
         * @return - Returns the component used for drawing the cell.
         */
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            // set the Font, Color, etc. 
            renderer.getTableCellRendererComponent(table, value,
                    isSelected, hasFocus, row, column);
            setForeground(renderer.getForeground());
            setBackground(renderer.getBackground());
            setBorder(renderer.getBorder());
            setFont(renderer.getFont());
            setText(renderer.getText());

            TableColumnModel columnModel = table.getColumnModel();
            setSize(columnModel.getColumn(column).getWidth(), 0);
            int height_wanted = (int) getPreferredSize().getHeight();
            addSize(table, row, column, height_wanted);
            height_wanted = findTotalMaximumRowSize(table, row);
            if (height_wanted != table.getRowHeight(row)) {
                table.setRowHeight(row, height_wanted);
            }
            return this;
        }

        /**
         * @param table - JTable object
         * @param row - The row index of the cell being drawn.
         * @param column - The column index of the cell being drawn.
         * @param height - Row cell height as int value This method will add
         * size to cell based on row and column number
         */
        private void addSize(JTable table, int row, int column, int height) {
            Map<Object, Map<Object, Integer>> rowsMap = tablecellSizes.get(table);
            if (rowsMap == null) {
                tablecellSizes.put(table, rowsMap = new HashMap<Object, Map<Object, Integer>>());
            }
            Map<Object, Integer> rowheightsMap = rowsMap.get(row);
            if (rowheightsMap == null) {
                rowsMap.put(row, rowheightsMap = new HashMap<Object, Integer>());
            }
            rowheightsMap.put(column, height);
        }

        /**
         * Look through all columns and get the renderer. If it is also a
         * TextAreaRenderer, we look at the maximum height in its hash table for
         * this row.
         *
         * @param table -JTable object
         * @param row - The row index of the cell being drawn.
         * @return row maximum height as integer value
         */
        private int findTotalMaximumRowSize(JTable table, int row) {
            int maximum_height = 0;
            Enumeration<TableColumn> columns = table.getColumnModel().getColumns();
            while (columns.hasMoreElements()) {
                TableColumn tc = columns.nextElement();
                TableCellRenderer cellRenderer = tc.getCellRenderer();
                if (cellRenderer instanceof TextAreaRenderer) {
                    TextAreaRenderer tar = (TextAreaRenderer) cellRenderer;
                    maximum_height = Math.max(maximum_height,
                            tar.findMaximumRowSize(table, row));
                }
            }
            return maximum_height;
        }

        /**
         * This will find the maximum row size
         *
         * @param table - JTable object
         * @param row - The row index of the cell being drawn.
         * @return row maximum height as integer value
         */
        private int findMaximumRowSize(JTable table, int row) {
            Map<Object, Map<Object, Integer>> rows = tablecellSizes.get(table);
            if (rows == null) {
                return 0;
            }
            Map<Object, Integer> rowheights = rows.get(row);
            if (rowheights == null) {
                return 0;
            }
            int maximum_height = 0;
            for (Map.Entry<Object, Integer> entry : rowheights.entrySet()) {
                int cellHeight = entry.getValue();
                maximum_height = Math.max(maximum_height, cellHeight);
            }
            return maximum_height;
        }
    }
}
