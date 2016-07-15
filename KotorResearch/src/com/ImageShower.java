package com;

import com.gooddies.graphics.ExtRectangle;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.*;

/**
 * @author Dmitry<br/>
 * Class for debug show of the bufferedImages
 */
public class ImageShower extends JFrame {
    private BufferedImage image;
    private static ImageShower window;
    private JPanel panel;

    private ImageShower() throws HeadlessException {
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                image = null;
                setVisible(false);
                dispose();
                window = null;
                panel = null;
            }
        });

        panel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                drawImage(g);
            }
        };

        getContentPane().add(panel);
    }

    private final Color blue = new Color(220, 220, 255);

    private void drawImage(Graphics g) {
        int rectSize = 20;
        int rectWidthCount = panel.getWidth() / rectSize + 1;
        int rectHeightCount = panel.getHeight() / rectSize + 1;
        for (int j = 0; j < rectHeightCount; j++) {
            for (int i = 0; i < rectWidthCount; i++) {
                boolean p;
                p = (i % 2) == 0;
                if ((j % 2) == 0) {
                    p = !p;
                }

                g.setColor(p ? blue : Color.white);

                g.fillRect(i * rectSize, j * rectSize, rectSize, rectSize);
            }
        }
        ExtRectangle rectangle = new ExtRectangle(0, 0, image.getWidth(), image.getHeight());
        rectangle.fitProportional(new Rectangle(0, 0, panel.getWidth(), panel.getHeight()));
        int x = (int) (panel.getWidth() / 2 - rectangle.getWidth() / 2);
        int y = (int) (panel.getHeight() / 2 - rectangle.getHeight() / 2);
        g.drawImage(image, x, y, (int) rectangle.getWidth(), (int) rectangle.getHeight(), panel);
    }

    public static void show(BufferedImage image) {
        if (window == null) {
            window = new ImageShower();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    window.setVisible(true);
                }
            });
        }

        window.setImage(image);
    }

    private void setImage(final BufferedImage image) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ImageShower.this.image = image;
                panel.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
                pack();
                invalidate();
                repaint();
            }
        });
    }
}
