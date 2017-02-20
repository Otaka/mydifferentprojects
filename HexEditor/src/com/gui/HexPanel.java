package com.gui;

import com.gooddies.events.Lookup;
import com.jogl.unpack.Unpack;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.JPanel;

/**
 * @author Dmitry
 */
public class HexPanel extends JPanel {
    private RandomAccessFile source;
    private int symbolHeight;
    private int symbolWidth;
    private int rowHeight = 20;
    private byte[] byteArray;
    private int currentRow;
    private int rowCount = 0;
    private final String[] numberStrings;
    private int byteCount;
    private int size = 0;
    private int selectedOffset = -1;
    private List<Chunk> chunks;
    private Color[] colors;

    public HexPanel() throws IOException {
        setOpaque(true);
        setBackground(Color.WHITE);
        setForeground(Color.DARK_GRAY);
        setPreferredSize(new Dimension(650, 500));
        setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                try {
                    onResized();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int x = e.getX();
                int y = e.getY() - (rowHeight / 2);
                int selectedRow = y / rowCount;
                int column = (x - 60) / (symbolWidth * 3);
                if (column <= 15) {
                    int offset = selectedRow * 16 + column + (currentRow * 16);
                    if (offset <= size) {
                        try {
                            onClick(offset);
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                    //System.out.println("Selected row " + selectedRow + " selected column " + column);
                }

            }
        });

        numberStrings = new String[256];
        for (int i = 0; i < 256; i++) {
            numberStrings[i] = String.format("%02x", i);
        }

        onResized();
    }

    public void setChunks(List<Chunk> chunks) {
        this.chunks = chunks;
        Set<Integer> labels = new HashSet<>();
        for (Chunk c : chunks) {
            labels.add(c.getLabelIndex());
        }
        colors = new Color[labels.size()];
        Random random = new Random(1);
        for (int i = 0; i < colors.length; i++) {
            colors[i] = new Color(random.nextFloat() / 2 + 0.5f, random.nextFloat() / 2 + 0.5f, random.nextFloat() / 2 + 0.5f);
        }
    }

    public String readString(int count) throws IOException {
        StringBuilder tempBuffer = new StringBuilder();
        for (int i = 0; i < count; i++) {
            int value = source.read();
            if (value == 0) {

            } else {
                tempBuffer.append((char) value);
            }
        }

        String result = tempBuffer.toString();
        tempBuffer.setLength(0);
        return result;
    }

    public String readNullTerminatedString() throws IOException {
        StringBuilder tempBuffer = new StringBuilder();
        while (true) {
            int value = source.read();
            if (value == -1) {
                tempBuffer.setLength(0);
                return null;
            }

            if (value == 0) {
                return tempBuffer.toString();
            }

            tempBuffer.append((char) value);
        }
    }

    public int readInt() throws IOException {
        return (source.read() & 0xFF) | ((source.read() & 0xFF) << 8) | ((source.read() & 0xFF) << 16) | ((source.read() & 0xFF) << 24);
    }

    public short readShort() throws IOException {
        return (short) ((source.read() & 0xFF) | ((source.read() & 0xFF) << 8));
    }

    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    private void onClick(int offset) throws IOException {
        selectedOffset = offset;
        repaint();
        Lookup.getDefault().fire(new InfoMessage("offset", "" + offset));
        Chunk chunk = getChunkByOffset(offset);
        clearIntersectionBorder(chunks);
        if (chunk != null) {
            markIntersection(chunks, chunk);
            String type = null;
            Object value = null;
            source.seek(chunk.getOffset());
            switch (chunk.getType()) {
                case 0:
                    type = "int";
                    value = "" + readInt();
                    break;
                case 1:
                    type = "String Fixed";
                    value = readString(chunk.getSize());
                    break;
                case 2:
                    type = "Float";
                    value = "" + readFloat();
                    break;
                case 3:
                    type = "Null Terminated String";
                    value = readNullTerminatedString();
                    break;
                case 4:
                    type = "Skip";
                    value = "";
                    break;
                case 5:
                    type = "Short";
                    value = "" + readShort();
                    break;
                case 6:
                    type = "Long";
                    value = "" + source.readLong();
                    break;
                case 7:
                    type = "Byte";
                    value = "" + source.read();
                    break;
                default:
                    type = "Unknown type " + type;
                    value = "";
            }

            Lookup.getDefault().fire(new InfoMessage("type", type));
            Lookup.getDefault().fire(new InfoMessage("value", "<html>" + value.toString() + "</html>"));
            Lookup.getDefault().fire(new InfoMessage("label", chunk.getLabel()));
            if (chunk.getUnpackPattern() != null) {
                String pattern = chunk.getUnpackPattern();
                try {
                    source.seek(chunk.getOffset());
                    byte[] buffer = new byte[chunk.getSize()];
                    source.read(buffer);
                    List parsedData = Unpack.unpack(pattern, new ByteArrayInputStream(buffer));
                    parsedData.add(0, pattern);
                    Lookup.getDefault().fire(new InfoMessageObject("parsedData", parsedData));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
                Lookup.getDefault().fire(new InfoMessageObject("parsedData", null));
            }
        } else {
            Lookup.getDefault().fire(new InfoMessage("type", ""));
            Lookup.getDefault().fire(new InfoMessage("value", ""));
            Lookup.getDefault().fire(new InfoMessage("label", ""));
            Lookup.getDefault().fire(new InfoMessageObject("parsedData", null));
        }

        source.seek(offset);
        int intValue = readInt();
        float floatValue = Float.intBitsToFloat(intValue);
        Lookup.getDefault().fire(new InfoMessage("intValue", "" + intValue));
        Lookup.getDefault().fire(new InfoMessage("floatValue", "" + floatValue));
    }

    private Chunk getChunkByOffset(int offset) {
        for (Chunk c : chunks) {
            if (c.getOffset() <= offset && (c.getOffset() + c.getSize()) > offset) {
                return c;
            }
        }
        return null;
    }

    private void onResized() throws IOException {
        int componentHeight = getHeight();
        rowCount = componentHeight / rowHeight;
        Lookup.getDefault().fire("rowCount", rowCount);
        byteArray = new byte[16 * rowCount];
        byteCount = byteArray.length;
        setRow(currentRow);
    }

    public int getRowCount() {
        return rowCount;
    }

    public void setSource(RandomAccessFile source) throws IOException {
        this.source = source;
        size = (int) source.length();
        setRow(0);
        repaint();
    }

    public void setRow(int row) throws IOException {
        int offset = row * 16;
        currentRow = row;
        if (source != null) {
            source.seek(offset);
            Arrays.fill(byteArray, (byte) 0);
            byteCount = source.read(byteArray);
        }

        repaint();
    }

    public void init() {
        symbolHeight = getFontMetrics(getFont()).getHeight();
        symbolWidth = getFontMetrics(getFont()).charWidth('1');
        rowHeight = symbolHeight + 1;
    }

    public void closeFile() throws IOException {
        source.close();
        source = null;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (source == null) {
            return;
        }

        int byteIndex = 0;
        int globalOffset = currentRow * 16;
        g.setColor(Color.blue);
        for (int i = 0; i < rowCount; i++) {
            g.drawString(String.format("%06d", (currentRow + i) * 16), 0, i * rowHeight + rowHeight);
        }

        int chunkIndex = -1;
        Chunk selectedChunk = null;
        for (int i = 0; i < chunks.size(); i++) {
            Chunk chunk = chunks.get(i);
            if ((chunk.getOffset() + chunk.getSize()) >= globalOffset) {
                chunkIndex = i;
                selectedChunk = chunk;
                break;
            }
        }

        g.setColor(Color.BLACK);
        endOfLoop:
        for (int j = 0; j < rowCount; j++) {
            int y = j * rowHeight + rowHeight;
            int x = 60;
            for (int i = 0; i < 16; i++, byteIndex++, globalOffset++) {
                if (byteIndex >= byteCount) {
                    break endOfLoop;
                }

                if (selectedChunk != null) {
                    if (globalOffset >= (selectedChunk.getOffset() + selectedChunk.getSize())) {
                        if ((chunkIndex + 1) >= chunks.size()) {
                            selectedChunk = null;
                        } else {
                            chunkIndex++;
                            selectedChunk = chunks.get(chunkIndex);
                        }
                    }

                    if (globalOffset >= selectedChunk.getOffset() && globalOffset < (selectedChunk.getOffset() + selectedChunk.getSize())) {
                        g.setColor(colors[selectedChunk.getLabelIndex()]);
                        g.fillRect(x, y - symbolHeight + 8, symbolWidth * 2, symbolHeight - 5);
                        if (selectedChunk.getOverlappedBorderColor() != null) {
                            g.setColor(selectedChunk.getOverlappedBorderColor());
                            int off = selectedChunk.getBorderOffset();
                            g.drawRect(x + off, y - symbolHeight + 8 + off, symbolWidth * 2 - off * 2, symbolHeight - 5 - off * 2);
                        }
                    }
                }

                int value = byteArray[byteIndex] & 0xFF;
                g.setColor(Color.BLACK);
                g.drawString(numberStrings[value], x, y);
                if (globalOffset == selectedOffset) {
                    g.drawRect(x, y - symbolHeight + 8, symbolWidth * 2, symbolHeight - 5);
                }

                x += symbolWidth * 3;
            }
        }

        g.setColor(Color.BLACK);
        byteIndex = 0;
        globalOffset = currentRow * 16;
        for (int j = 0; j < rowCount; j++) {
            int y = j * rowHeight + rowHeight;
            int x = 490;
            for (int i = 0; i < 16; i++, byteIndex++, globalOffset++) {
                if (byteIndex >= byteCount) {
                    return;
                }

                char value = (char) byteArray[byteIndex];
                g.setColor(Color.BLACK);
                g.drawString("" + value, x, y);
                if (globalOffset == selectedOffset) {
                    g.drawRect(x, y - symbolHeight + 8, symbolWidth, symbolHeight - 5);
                }

                x += symbolWidth;
            }
        }
    }

    private void createOverlappedBorder(Chunk c) {
        if (c.getOverlappedBorderColor() == null) {
            c.setOverlappedBorderColor(new Color((int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
            c.setBorderOffset((int) (Math.random() * 5));
        }
    }

    private void clearIntersectionBorder(List<Chunk> chunks) {
        for (Chunk c : chunks) {
            c.setOverlappedBorderColor(null);
            c.setBorderOffset(-1);
        }
    }

    private void markIntersection(List<Chunk> chunks, Chunk selectedChunk) {
        for (Chunk c2 : chunks) {
            if (selectedChunk != c2) {
                if (ifChunksIntersects(selectedChunk, c2)) {
                    createOverlappedBorder(selectedChunk);
                    createOverlappedBorder(c2);
                }
            }
        }
    }

    private static boolean ifChunksIntersects(Chunk c1, Chunk c2) {
        int pos_A_left = c1.getOffset();
        int pos_A_right = c1.getOffset() + c1.getSize();
        int pos_B_left = c2.getOffset();
        int pos_B_right = c2.getOffset() + c2.getSize();
        return pos_B_left < pos_A_right && pos_A_left < pos_B_right;
    }

}
