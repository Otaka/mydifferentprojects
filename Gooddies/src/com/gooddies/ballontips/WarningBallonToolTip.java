package com.gooddies.ballontips;

import java.awt.Color;
import javax.swing.JComponent;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.ModernBalloonStyle;

/**
 * @author Dmitry Savchenko
 */
public class WarningBallonToolTip extends CustomBallonToolTip {

    public WarningBallonToolTip(JComponent parent) {
        super(parent);

    }

    @Override
    public BalloonTipStyle createStyle() {
        ModernBalloonStyle moderStyle = new ModernBalloonStyle(10, 10, new Color(240, 240, 240), new Color(255, 255, 150), new Color(255, 128, 0));
        return moderStyle;
    }
}
