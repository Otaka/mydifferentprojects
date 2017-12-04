package com.gooddies.ballontips;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.Timer;
import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.MinimalBalloonStyle;
import net.java.balloontip.styles.ModernBalloonStyle;

/**
 * @author Dmitry Savchenko
 */
public class CustomBallonToolTip extends BalloonTip {

    protected Timer timer;

    public CustomBallonToolTip(JComponent parent) {
        super(parent, "", new MinimalBalloonStyle(Color.yellow, 1), BalloonTip.Orientation.LEFT_ABOVE, BalloonTip.AttachLocation.ALIGNED, 20, 10, false);
        setStyle(createStyle());
        super.setVisible(false);
        setFocusable(true);
        setIcon(null);
        addFocusListener(new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                setVisible(false);
            }
        });
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
            stopTimer();
            timer = new Timer(5000, new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    setVisible(false);
                    stopTimer();
                }
            });
            timer.start();
            requestFocusInWindow();
        } else {
            stopTimer();
        }
    }

    public BalloonTipStyle createStyle() {
        ModernBalloonStyle moderStyle = new ModernBalloonStyle(10, 10, new Color(255, 255, 255), new Color(255, 255, 255), Color.BLACK);
        return moderStyle;
    }
}
