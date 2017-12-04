package com.gooddies.swing.swypepanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * @author sad
 */
public class SwypePanel extends JPanel {

    private SwitchablePanel component;
    private BufferedImage newImage;
    private BufferedImage oldImage;
    private Animation animation;
    private float xOffset = 0;
    private Timer timer;
    private boolean endAnimation = true;
    private HeaderPanel headerPanel;
    private final List<SwitchablePanel> componentStack = new ArrayList<>();
    private PropertyChangeListener titleChangeListener;
    private JPanel mainPanel;
    private boolean isBack;

    public static enum Animation {

        LEFT_RIGHT, RIGHT_LEFT
    };

    public SwypePanel() {
        init();
    }

    public final void init() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(true);
        mainPanel = new JPanel(new BorderLayout(0, 0)) {
            @Override
            public void paint(Graphics g) {
                if (endAnimation) {
                    super.paint(g);
                } else {
                    g.drawImage(oldImage, (int) xOffset, 0, this);
                    if (animation == Animation.LEFT_RIGHT) {
                        g.drawImage(newImage, (int) (xOffset - mainPanel.getWidth()), 0, this);
                    } else if (animation == Animation.RIGHT_LEFT) {
                        g.drawImage(newImage, (int) (xOffset + mainPanel.getWidth()), 0, this);
                    }
                }
            }
        };
        super.add(mainPanel, BorderLayout.CENTER);
        headerPanel = new HeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        headerPanel.addOnBackEvent(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                back();
            }
        });
        titleChangeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("panelTitle")) {
                    headerPanel.setTitle((String) evt.getNewValue());
                }
            }
        };
    }

    @Override
    public Component add(Component comp) {
        return mainPanel.add(comp);
    }

    private void back() {
        if (!endAnimation) {
            return;
        }
        if (componentStack.isEmpty()) {
            return;
        }
        isBack = true;
        SwitchablePanel oldComponent = componentStack.get(componentStack.size() - 1);
        componentStack.remove(componentStack.size() - 1);
        setComponent(oldComponent, Animation.LEFT_RIGHT);
    }

    public void pushComponent(SwitchablePanel component) {
        if (component == this.component) {
            return;
        }
        isBack = false;
        SwitchablePanel oldComponent = setComponent(component, Animation.RIGHT_LEFT);
        if (oldComponent != null) {
            componentStack.add(oldComponent);
            if (componentStack.size() > 10) {
                componentStack.remove(1);
            }
        }
    }

    private SwitchablePanel setComponent(SwitchablePanel comp, Animation animation) {
        if (component != null) {
            mainPanel.remove(component);
            component.removePropertyChangeListener(titleChangeListener);
        }
        comp.addPropertyChangeListener("panelTitle", titleChangeListener);
        comp.setVisible(true);
        headerPanel.setTitle(comp.getTitle());
        if (component == null) {
            SwitchablePanel oldComponent = null;
            this.component = comp;
            mainPanel.add(component, BorderLayout.CENTER);
            repaintWithAnimation();
            return oldComponent;
        } else {
            comp.setVisible(false);
            component.setVisible(false);
            this.animation = animation;
            comp.setSize(mainPanel.getWidth(), mainPanel.getHeight());
            newImage = ScreenImage.createImage(comp);
            oldImage = ScreenImage.createImage(component);
            SwitchablePanel oldComponent = component;
            setAnimatingTimer();
            //remove(component);
            component = comp;
            mainPanel.add(component, BorderLayout.CENTER);
            repaintWithAnimation();
            return oldComponent;
        }
    }

    private void endAnimation() {
        endAnimation = true;
        oldImage = null;
        newImage = null;
        animation = null;
        component.setVisible(true);
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    component.onShow(isBack);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        mainPanel.repaint();
    }

    private void setAnimatingTimer() {
        float stepsCount = 10;
        final float dx = mainPanel.getWidth() / stepsCount;
        xOffset = 0;
        endAnimation = false;
        timer = new Timer(10, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (animation == Animation.LEFT_RIGHT) {
                    xOffset += dx;
                    if (xOffset >= mainPanel.getWidth()) {
                        timer.stop();
                        endAnimation();
                    }
                }
                if (animation == Animation.RIGHT_LEFT) {
                    xOffset -= dx;
                    if (xOffset <= -mainPanel.getWidth()) {
                        timer.stop();
                        endAnimation();
                    }
                }

                repaintWithAnimation();
            }
        });
        timer.setRepeats(true);
        timer.start();
    }

    private void repaintWithAnimation() {
        mainPanel.repaint();
    }
}
