package com.gooddies.ballontips;

import java.awt.Color;
import java.awt.event.*;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.MinimalBalloonStyle;
import net.java.balloontip.styles.ModernBalloonStyle;
import net.java.balloontip.utils.ToolTipUtils;

/**
 *
 * @author Dmitry Savchenko
 */
public class ToolTipHandler extends BalloonTip {
    protected Timer timer;
    protected int DELAY = 5000;
    public static final Color INFORMATION_COLOR_TOP = new Color(222, 237, 252);
    public static final Color INFORMATION_COLOR_BOTTOM = new Color(170, 211, 250);
    public static final Color ERROR_COLOR_TOP = new Color(255, 240, 240);
    public static final Color ERROR_COLOR_BOTTOM = new Color(255, 150, 150);
    public static final Color WARNING_COLOR_BOTTOM = new Color(255, 246, 125);
    public static final Color WARNING_COLOR_TOP = new Color(253, 252, 192);
    public static final int INFORMATION_TOOLTIP = 1;
    public static final int ERROR_TOOLTIP = 2;
    public static final int WARNING_TOOLTIP = 3;
    private boolean isCloseButton = false;

    public ToolTipHandler(JComponent parent, boolean close) {
        super(parent, "", new MinimalBalloonStyle(Color.yellow, 1), BalloonTip.Orientation.LEFT_ABOVE, BalloonTip.AttachLocation.ALIGNED, 20, 10, close, true);
        isCloseButton = close;
        setStyle(createStyle(INFORMATION_TOOLTIP));
        super.setVisible(false);
        setFocusable(true);
        setIcon(null);
        if(close){
            setCloseButtonActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                    stopTimer();
                }
            });
        }
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setVisible(false);
            }
        });
    }

    public ToolTipHandler(JComponent parent, Orientation orientation) {
        super(parent, "", new MinimalBalloonStyle(Color.yellow, 1), orientation, BalloonTip.AttachLocation.ALIGNED, 20, 10, false, true);
        setStyle(createStyle(INFORMATION_TOOLTIP));
        super.setVisible(false);
        setFocusable(true);
        setIcon(null);
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setVisible(false);
            }
        });

    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }
    }

    @Override
    public void setVisible(boolean bln) {
        super.setVisible(bln);
        if (bln == true) {
            if (!isCloseButton) {
                stopTimer();
                timer = new Timer(DELAY, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setVisible(false);
                        stopTimer();
                    }
                });
                timer.start();
            }
            requestFocusInWindow();
        } else {
            stopTimer();
        }
    }

    private static void errorTooltip(JComponent parent, String text) {
        ToolTipHandler custom = new ToolTipHandler(parent, false);
        custom.setText(text);
        custom.setStyle(createStyle(ERROR_TOOLTIP));
        custom.setVisible(true);
    }

    public static void error(JComponent parent, String text, boolean showAlways) {
        ToolTipHandler custom = new ToolTipHandler(parent, false);
        custom.setText(text);
        custom.setStyle(createStyle(ERROR_TOOLTIP));

        if (showAlways) {
            ToolTipUtils.balloonToToolTip(custom, 500, custom.DELAY);
        }

        custom.setVisible(true);
    }

    public static ToolTipHandler errorHandler(JComponent parent, String text) {

        ToolTipHandler custom = new ToolTipHandler(parent, false);
        custom.setText(text);
        custom.setStyle(createStyle(ERROR_TOOLTIP));
        return custom;

    }

    public static ToolTipHandler errorHandler(JComponent parent, String text, boolean showAlways) {

        ToolTipHandler custom = new ToolTipHandler(parent, false);
        custom.setText(text);
        custom.setStyle(createStyle(ERROR_TOOLTIP));

        if (showAlways) {
            ToolTipUtils.balloonToToolTip(custom, 500, custom.DELAY);
        }
        return custom;
    }

    private static void warningTooltip(JComponent parent, String text) {

        ToolTipHandler custom = new ToolTipHandler(parent, false);

        custom.setText(text);
        custom.setVisible(true);
        custom.setStyle(createStyle(WARNING_TOOLTIP));

    }

    public static void warning(JComponent parent, String text, boolean showAlways) {

        ToolTipHandler custom = new ToolTipHandler(parent, false);

        custom.setText(text);
        custom.setVisible(true);
        custom.setStyle(createStyle(WARNING_TOOLTIP));
        if (showAlways) {
            ToolTipUtils.balloonToToolTip(custom, 500, custom.DELAY);
        }
    }

    public static ToolTipHandler warningHandler(JComponent parent, String text) {

        ToolTipHandler custom = new ToolTipHandler(parent, false);

        custom.setText(text);
        custom.setStyle(createStyle(WARNING_TOOLTIP));

        return custom;
    }

    private static void informationTooltip(JComponent parent, String text) {

        ToolTipHandler custom = new ToolTipHandler(parent, false);

        custom.setText(text);
        custom.setVisible(true);
        custom.setStyle(createStyle(INFORMATION_TOOLTIP));


    }

    public static void information(JComponent parent, String text, boolean showCloseButton) {

        ToolTipHandler custom = new ToolTipHandler(parent, showCloseButton);

        custom.setText(text);
        custom.setVisible(true);
        custom.setStyle(createStyle(INFORMATION_TOOLTIP));
    }

    public static void information(JComponent parent, String text, boolean showAlways, Orientation orientation) {

        ToolTipHandler custom = new ToolTipHandler(parent, orientation);

        custom.setText(text);
        custom.setVisible(true);
        custom.setStyle(createStyle(INFORMATION_TOOLTIP));

        if (showAlways) {
            ToolTipUtils.balloonToToolTip(custom, 500, custom.DELAY);
        }
    }

    public static ToolTipHandler informationHandler(JComponent parent, String text) {
        ToolTipHandler custom = new ToolTipHandler(parent, false);
        custom.setText(text);
        custom.setStyle(createStyle(INFORMATION_TOOLTIP));

        return custom;
    }

    public static ToolTipHandler informationHandler(JComponent parent, String text, int delay) {
        ToolTipHandler custom = new ToolTipHandler(parent, false);
        custom.DELAY = delay;
        custom.setText(text);
        custom.setStyle(createStyle(INFORMATION_TOOLTIP));

        return custom;
    }

    public static ToolTipHandler informationHandler(JComponent parent, String text, boolean showAlways) {

        ToolTipHandler custom = new ToolTipHandler(parent, false);
        custom.setText(text);
        custom.setStyle(createStyle(INFORMATION_TOOLTIP));

        if (showAlways) {
            ToolTipUtils.balloonToToolTip(custom, 500, custom.DELAY);
        }

        return custom;
    }

    public static ToolTipHandler informationHandler(JComponent parent, String text, boolean showAlways, Orientation orientation) {

        ToolTipHandler custom = new ToolTipHandler(parent, orientation);
        custom.setText(text);
        custom.setStyle(createStyle(INFORMATION_TOOLTIP));

        if (showAlways) {
            ToolTipUtils.balloonToToolTip(custom, 500, custom.DELAY);
        }

        return custom;
    }

    /**
     * Is able to appear after confirm messages
     */
    public static void error(final JComponent parent, final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                errorTooltip(parent, text);
            }
        });
    }

    /**
     * Is able to appear after confirm messages
     */
    public static void warning(final JComponent parent, final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                warningTooltip(parent, text);
            }
        });
    }

    /**
     * Is able to appear after confirm messages
     */
    public static void information(final JComponent parent, final String text) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                informationTooltip(parent, text);
            }
        });
    }

    public static BalloonTipStyle createStyle(int message_type) {
        ModernBalloonStyle moderStyle = null;
        if (message_type == WARNING_TOOLTIP) {
            moderStyle = new ModernBalloonStyle(10, 10, WARNING_COLOR_TOP, WARNING_COLOR_BOTTOM, WARNING_COLOR_TOP.darker());
        }
        if (message_type == ERROR_TOOLTIP) {
            moderStyle = new ModernBalloonStyle(10, 10, ERROR_COLOR_TOP, ERROR_COLOR_BOTTOM, ERROR_COLOR_TOP.darker());
        }
        if (message_type == INFORMATION_TOOLTIP) {
            moderStyle = new ModernBalloonStyle(10, 10, INFORMATION_COLOR_TOP, INFORMATION_COLOR_BOTTOM, INFORMATION_COLOR_TOP.darker());
        }

        return moderStyle;
    }
}
