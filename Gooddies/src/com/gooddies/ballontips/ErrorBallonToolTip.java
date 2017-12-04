package com.gooddies.ballontips;

import java.awt.Color;
import javax.swing.JComponent;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.ModernBalloonStyle;

/**
 * @author Dmitry Savchenko
 */
public class ErrorBallonToolTip extends CustomBallonToolTip {

    public ErrorBallonToolTip(JComponent parent) {
        super(parent);
    }

    @Override
    public BalloonTipStyle createStyle() {
        ModernBalloonStyle moderStyle = new ModernBalloonStyle(10, 10, new Color(255, 240, 240), new Color(255, 150, 150), Color.RED);
        return moderStyle;
    }
}
