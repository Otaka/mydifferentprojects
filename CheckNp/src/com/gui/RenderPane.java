package com.gui;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.serializers.CompatibleFieldSerializer;
import com.gui.instruments.AbstrInstrument;
import com.n.DetectionN;
import com.settings.editor.builder.CategoryProperties;
import com.settings.editor.builder.SettingsDialogBuilder;
import com.utils.ColorSerializer;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class RenderPane extends JPanel {
    private final List<DetectionN> nodes = new ArrayList<>();
    private Node selectedNode = null;
    private AbstrInstrument selectedInstrument;

    public RenderPane() {
        setFont(getFont().deriveFont((float) 9));
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                    deleteSelectedNode();
                }
            }
        });
        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (selectedInstrument != null) {
                    selectedInstrument.mouseMove(e.getX(), e.getY());
                }
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedInstrument != null) {
                    selectedInstrument.mouseMove(e.getX(), e.getY());
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    if (selectedInstrument != null && selectedNode != null) {
                        settings();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                requestFocus();
                if (selectedInstrument != null) {
                    selectedInstrument.mouseDown(e.getX(), e.getY(), e.getButton());
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (selectedInstrument != null) {
                    selectedInstrument.mouseUp(e.getX(), e.getY(), e.getButton());
                }
            }
        });
    }

    private void deleteSelectedNode() {
        if (selectedNode != null) {
            nodes.remove(selectedNode);
        }

        for (Node node : nodes) {
            Connection connection = node.getConnection(selectedNode);
            if (connection != null) {
                node.getConnections().remove(connection);
            }
        }
        repaint();
    }

    public void load(File file) throws FileNotFoundException {
        if (!file.exists()) {
            throw new RuntimeException("File not found " + file.getAbsolutePath());
        }
        Kryo kryo = new Kryo();
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
        kryo.register(Color.class, new ColorSerializer());
        try (Input input = new Input(new FileInputStream(file))) {
            List<DetectionN> newNodes = kryo.readObject(input, nodes.getClass());
            nodes.clear();
            nodes.addAll(newNodes);
        }

        repaint();
    }

    public void save(List<DetectionN> nodes, File file) throws FileNotFoundException {
        Kryo kryo = new Kryo();
        kryo.setDefaultSerializer(CompatibleFieldSerializer.class);
        kryo.register(Color.class, new ColorSerializer());
        try (Output output = new Output(new FileOutputStream(file))) {
            kryo.writeObject(output, nodes);
        }
    }

    private void settings() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final JDialog dialog = new SettingsDialogBuilder()
                        .setProperty(selectedNode)
                        .setCategoryProperties("someCategory", new CategoryProperties().setScroll(true))
                        .show();

                dialog.addWindowFocusListener(new WindowFocusListener() {
                    @Override
                    public void windowGainedFocus(WindowEvent e) {
                    }

                    private boolean checkIfThisDialogInParentHierarchy(Container container) {
                        while (container != null) {
                            if (container == dialog) {
                                return true;
                            }

                            container = container.getParent();
                        }

                        return false;
                    }

                    @Override
                    public void windowLostFocus(WindowEvent e) {
                        if (!checkIfThisDialogInParentHierarchy(e.getOppositeWindow())) {
                            dialog.setVisible(false);
                            dialog.dispose();
                        }
                    }
                });
            }
        });
    }

    public void setSelectedInstrument(AbstrInstrument selectedInstrument) {
        this.selectedInstrument = selectedInstrument;
    }

    public Node getSelectedNode() {
        return selectedNode;
    }

    public void setSelectedNode(Node selectedNode) {
        this.selectedNode = selectedNode;
    }

    public Node getNodeByXY(int x, int y) {
        for (Node node : nodes) {
            int dist = (int) lineDistance(x, y, node.getX(), node.getY());
            if (dist <= node.getRadius()) {
                return node;
            }
        }

        return null;
    }

    private float lineDistance(int x1, int y1, int x2, int y2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public List<DetectionN> getNodes() {
        return nodes;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        for (Node n : nodes) {
            n.paint(g, selectedNode == n);
        }
    }
}
