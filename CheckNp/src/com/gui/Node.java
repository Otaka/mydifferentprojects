package com.gui;

import com.settings.editor.components.annotations.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {

    private int x;
    private int y;
    @PropertyColor(name = "Цвет")
    private Color color;
    @PropertyText(name = "Текст", multiline = false)
    private String title;

    private StringRow[] titleParsedRows;
    @PropertyInt(name = "Радиус", min = 0, max = 100)
    private int radius = 4;
    private final List<Connection> connections = new ArrayList<>();

    /**
     * Add connection to the node. If this nodeToConnect is already connected to
     * this - nothing changed
     */
    public Connection addConnectToNode(Node nodeToConnect) {
        Connection c = getConnection(nodeToConnect);
        if (c == null) {
            c = addConnectToNodeFast(nodeToConnect);
        }

        return c;
    }

    /**
     * Add connection without checking if this node is already connected
     */
    public Connection addConnectToNodeFast(Node node) {
        Connection c = new Connection(node);
        connections.add(c);
        return c;
    }

    public Connection getConnection(Node node) {
        for (Connection c : connections) {
            if (c.getNode() == node) {
                return c;
            }
        }

        return null;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public boolean isConnected(Node node) {
        return getConnection(node) != null;
    }

    public boolean removeConnection(Node node) {
        Connection connection = getConnection(node);
        if (connection != null) {
            connections.remove(connection);
            return true;
        }
        return false;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public StringRow[] getTitleParsedRows() {
        return titleParsedRows;
    }

    public void setTitleParsedRows(StringRow[] titleParsedRows) {
        this.titleParsedRows = titleParsedRows;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        setTitleParsedRows(null);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void paint(Graphics g, boolean isSelected) {
        if (isSelected) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(getColor());
        }

        int tRadius = getRadius();
        g.fillOval(getX() - tRadius, getY() - tRadius, tRadius + tRadius, tRadius + tRadius);

        if (getTitleParsedRows() == null) {
            setTitleParsedRows(parseTitleRows(g, getTitle()));
        }

        int tY = (int) (getY() + tRadius);
        for (StringRow row : getTitleParsedRows()) {
            tY += row.getHeight();
            g.drawString(row.getText(), getX() - row.getWidth() / 2, tY);
        }

        for (Connection connection : getConnections()) {
            drawConnection(this, connection, g);
        }
    }

    protected void drawConnection(Node node, Connection connection, Graphics g) {
        int x1 = node.getX();
        int y1 = node.getY();
        int x2 = connection.getNode().getX();
        int y2 = connection.getNode().getY();
        float distance = lineDistance(x1, y1, x2, y2);
        int xFinish = lerp(x1, x2, (distance - connection.getNode().getRadius() - 1) / distance);
        int yFinish = lerp(y1, y2, (distance - connection.getNode().getRadius() - 1) / distance);

        g.drawLine(x1, y1, xFinish, yFinish);
        drawArrow(x1, y1, xFinish, yFinish, 10, g);
    }

    protected void drawArrow(int x1, int y1, int x2, int y2, int length, Graphics g) {
        float dist = lineDistance(x1, y1, x2, y2);
        if (dist < length) {
            length = (int) dist;
        }

        double r = Math.atan2(y2 - y1, x2 - x1);
        int tX = (int) (Math.cos(Math.toRadians(Math.toDegrees(r) + 170)) * length + x2);
        int tY = (int) (Math.sin(Math.toRadians(Math.toDegrees(r) + 170)) * length + y2);
        g.drawLine(x2, y2, tX, tY);
        tX = (int) (Math.cos(Math.toRadians(Math.toDegrees(r) + 190)) * length + x2);
        tY = (int) (Math.sin(Math.toRadians(Math.toDegrees(r) + 190)) * length + y2);
        g.drawLine(x2, y2, tX, tY);
    }

    protected int lerp(int x1, int x2, float t) {
        return (int) (x1 * (1 - t) + x2 * t);
    }

    protected float lineDistance(int x1, int y1, int x2, int y2) {
        return (float) Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    protected StringRow[] parseTitleRows(Graphics g, String text) {
        String[] lines = text.split("[\\r\\n]");
        StringRow[] rows = new StringRow[lines.length];
        FontMetrics fontMetrix = g.getFontMetrics(g.getFont());
        for (int i = 0; i < rows.length; i++) {
            String line = lines[i].trim();
            Rectangle2D rect = fontMetrix.getStringBounds(line, g);
            StringRow row = new StringRow(line, (int) rect.getWidth(), (int) rect.getHeight());
            rows[i] = row;
        }

        return rows;
    }
}
