package com.gooddies.graphics;

import com.gooddies.events.BooleanEvent;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.Icon;
import javax.swing.JLabel;

/**
 * @author dsavchenko
 */
public class LinkIconLabel extends JLabel {

    private static final long serialVersionUID = 1L;
    private Icon defaultIcon = null;
    private Icon rolloverIcon = null;
    private Icon pressedIcon = null;
    private String link = "";
    private BooleanEvent event = null;
    private MouseListener mouseEvent;

    public LinkIconLabel() {
    }

    public LinkIconLabel(String link, Icon defaultIcon, Icon rolloverIcon, Icon pressedIcon) {
        this.link = link;
        this.defaultIcon = defaultIcon;
        this.rolloverIcon = rolloverIcon;
        this.pressedIcon = pressedIcon;
        init();
    }

    public LinkIconLabel(String link, InteractiveIcon icon) {
        this(link, icon.getDefaultIcon(), icon.getRolloverIcon(), icon.getPressedIcon());
    }

    private void init() {
        if (defaultIcon != null) {
            Dimension d = new Dimension(defaultIcon.getIconWidth() + 2, defaultIcon.getIconHeight() + 2);
            setPreferredSize(d);
            setMaximumSize(d);
        }

        setIcon(defaultIcon);
        setText("");
        addMouseListener();
    }

    private void addMouseListener() {
        if (mouseEvent != null) {
            removeMouseListener(mouseEvent);
        }
        mouseEvent = new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isEnabled()) {
                    return;
                }
                if (LinkIconLabel.this.rolloverIcon != null) {
                    setIcon(LinkIconLabel.this.rolloverIcon);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isEnabled()) {
                    return;
                }
                if (LinkIconLabel.this.defaultIcon != null) {
                    setIcon(LinkIconLabel.this.defaultIcon);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                if (!isEnabled()) {
                    return;
                }
                if (LinkIconLabel.this.pressedIcon != null) {
                    setIcon(LinkIconLabel.this.pressedIcon);
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                if (!isEnabled()) {
                    return;
                }
                if (checkPointInComponent(e.getX(), e.getY())) {
                    if (event != null && event.fireEvent(LinkIconLabel.this) == true) {
                        executeAction();
                    }
                    setIcon(LinkIconLabel.this.rolloverIcon);
                } else {
                    setIcon(LinkIconLabel.this.defaultIcon);
                }
            }
        };
        addMouseListener(mouseEvent);
    }

    public void setInteractiveIcon(InteractiveIcon icon) {
        this.defaultIcon = icon.getDefaultIcon();
        this.rolloverIcon = icon.getRolloverIcon();
        this.pressedIcon = icon.getPressedIcon();
        addMouseListener();
    }

    private boolean checkPointInComponent(int x, int y) {
        return x >= 0 && y >= 0 && x <= getWidth() && y <= getHeight();
    }

    private void executeAction() {
        if (link != null && link.length() != 0) {
            startHelp(link);
        }
    }

    private void startHelp(String link) {
        try {
            String[] cmd = new String[4];
            cmd[0] = "cmd.exe";
            cmd[1] = "/C";
            cmd[2] = "start";
            cmd[3] = link;
            Process exec = Runtime.getRuntime().exec(cmd);
            exec = null;
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
    }

    public void addClickEvent(BooleanEvent event) {
        this.event = event;
    }
}
