package com.gui;

import com.gooddies.events.Lookup;
import com.gooddies.swing.ChunkedTextProcessor;
import java.awt.BorderLayout;
import java.awt.HeadlessException;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;

/**
 * @author Dmitry
 */
public class MainWindow extends JFrame {
    private final HexPanel hexPanel;
    private final JScrollBar scroll;
    private File currentFile;
    private final InfoPanel infoPanel;
    private final ChunkedTextProcessor titleChunkedProcessor;

    public MainWindow() throws HeadlessException, IOException {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JPanel scrollPanel = new JPanel(new BorderLayout());
        hexPanel = new HexPanel();
        hexPanel.init();
        scrollPanel.add(hexPanel);
        scroll = new JScrollBar(JScrollBar.VERTICAL);
        scroll.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                try {
                    hexPanel.setRow(e.getValue());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        scrollPanel.add(scroll, BorderLayout.EAST);
        add(scrollPanel);
        scrollPanel.addMouseWheelListener(new MouseAdapter() {

            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                scroll.setValue(scroll.getValue() + e.getUnitsToScroll());
            }
        });
        infoPanel = new InfoPanel();
        add(infoPanel, BorderLayout.EAST);
        titleChunkedProcessor = new ChunkedTextProcessor(new ChunkedTextProcessor.OnTextChanged() {

            @Override
            public void textChanged(String text) {
                setTitle(text);
            }
        });
        titleChunkedProcessor.setTitles("title", "HexEditor", "-1", " - ", "fileName", "", "modify", "");
        titleChunkedProcessor.setTitle("title", "HexEditor");
        Lookup.getDefault().addChangeEvent("rowCount", new Lookup.LookupEventChangedValue<Integer>() {

            @Override
            public void change(Integer newValue) {
                if (currentFile != null) {
                    scroll.setMaximum((int) (currentFile.length() / 16));
                }
            }
        });
    }

    public void loadFileInternal(File file) throws IOException {
        if (currentFile != null) {
            hexPanel.closeFile();
        }
        titleChunkedProcessor.setTitle("fileName", file.getName());
        scroll.setValue(0);
        scroll.setMaximum((int) (file.length() / 16) - 5);
        File descriptionFile = new File(file.getAbsolutePath() + ".descr");
        if (descriptionFile.exists()) {
            List<Chunk> chunks = loadDescriptionFile(descriptionFile);
            hexPanel.setChunks(chunks);
        } else {
            hexPanel.setChunks(new ArrayList<Chunk>());
        }
        hexPanel.setSource(new RandomAccessFile(file, "rw"));
        currentFile = file;

    }

    private List<Chunk> loadDescriptionFile(File file) throws FileNotFoundException, IOException {
        List<Chunk> chunks = new ArrayList<>(100);
        RandomAccessFile rf = new RandomAccessFile(file, "r");
        int lineTableOffset = rf.readInt();
        rf.seek(lineTableOffset);
        Map<Integer, String> lineMap = new HashMap<>();
        int currentIndex = -1;
        Pattern binaryPattern = Pattern.compile("^(.*)\\$\\{(.+?)}");
        while (rf.length() > rf.getFilePointer()) {
            currentIndex++;
            int size = rf.readInt();
            byte[] lineBuffer = new byte[size];
            rf.read(lineBuffer);
            String label = new String(lineBuffer);
            lineMap.put(currentIndex, label);
        }

        rf.seek(4);
        while (rf.getFilePointer() < lineTableOffset) {
            int labelIndex = rf.readInt();
            int offset = rf.readInt();
            int size = rf.readInt();
            int type = rf.readInt();
            String label = lineMap.get(labelIndex);
            String unpackPattern = null;
            Matcher matcher = binaryPattern.matcher(label);
            if (matcher.find()) {
                label = matcher.group(1);
                unpackPattern = matcher.group(2);
            }
            Chunk chunk = new Chunk(offset, label, size, type, labelIndex);
            chunk.setUnpackPattern(unpackPattern);
            chunks.add(chunk);
        }
        Collections.sort(chunks, new Comparator<Chunk>() {

            @Override
            public int compare(Chunk o1, Chunk o2) {
                return o1.getOffset() - o2.getOffset();
            }
        });

        return chunks;
    }
}
