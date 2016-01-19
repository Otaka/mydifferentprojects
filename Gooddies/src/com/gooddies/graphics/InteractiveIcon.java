package com.gooddies.graphics;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * @author dsavchenko
 */
public class InteractiveIcon implements Icon {

    private ImageIcon rollover = null;
    private ImageIcon defaultIcon = null;
    private ImageIcon pressed = null;
    private String path = null;

    /**
     * @param path path to the icon file(jpg, bmp, png...). If exists file
     * path_rollover.jpg(bmp, png...), class will load it as rollover
     * image(getRolloverIcon()). If exists file path_pressed.jpg(bmp, png...)
     * class will load it as pressed image(getPressedIcon()).
     */
    public InteractiveIcon(String path) {
        this.path = path;
        try {
            BufferedImage image = ImageIO.read(new File(path));
            defaultIcon = new ImageIcon(image);
        } catch (IIOException e) {
            try {
                defaultIcon = new ImageIcon(InteractiveIcon.class.getResource(path));
            } catch (Exception ex) {
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public InteractiveIcon(BufferedImage icon) {
        defaultIcon = new ImageIcon(icon);
    }

    public InteractiveIcon(ImageIcon icon) {
        defaultIcon = icon;
    }

    public Icon getDefaultIcon() {
        return defaultIcon;
    }

    public void clearRolloverPressedIcons(){
        rollover=null;
        pressed=null;
    }
    
    public Icon getRolloverIcon() {
        if (rollover == null) {
            String rolloverPath = getFileName(path) + "_rollover" + getFileExt(path);
            if (InteractiveIcon.class.getResource(rolloverPath) != null) {
                rollover = new ImageIcon(InteractiveIcon.class.getResource(rolloverPath));
            } else {
                rollover = createRolloverIcon(defaultIcon);
            }
        }

        return rollover;
    }

    public Icon getPressedIcon() {
        if (pressed == null) {
            String pressedPath = getFileName(path) + "_pressed" + getFileExt(path);
            if (InteractiveIcon.class.getResource(pressedPath) != null) {
                pressed = new ImageIcon(InteractiveIcon.class.getResource(pressedPath));
            } else {
                pressed = createPressedIcon(defaultIcon);
            }
        }
        return pressed;
    }

    private String getFileName(String path) {
        if (path == null) {
            return "";
        }
        int index = path.lastIndexOf('.');
        if (index > 0 && index <= path.length() - 2) {
            return path.substring(0, index);
        }
        return "";
    }

    private String getFileExt(String path) {
        if (path == null) {
            return "";
        }
        int index = path.lastIndexOf('.');
        if (index > 0 && index <= path.length() - 2) {
            return path.substring(index);
        }
        return "";
    }

    private ImageIcon mirrorHorizontal(ImageIcon icon) {
        BufferedImage image = convertImageToBufferedImage(icon);
        Graphics2D g2d = (Graphics2D) image.getGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        int[] buf = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        int w = image.getWidth();
        int h = image.getHeight();
        int centerX = w / 2;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < centerX; x++) {
                int v = buf[x];
                buf[x] = buf[w - x];
                buf[w - x] = v;
            }
        }

        ImageIcon resultIcon = new ImageIcon(image);
        return resultIcon;
    }

    private ImageIcon mirrorVertical(ImageIcon icon) {
        BufferedImage image = convertImageToBufferedImage(icon);
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        int height=icon.getIconHeight();
        tx.translate(0, -height);
        AffineTransformOp op = new AffineTransformOp(tx,
                AffineTransformOp.TYPE_BICUBIC);
        image = op.filter(image, null);
        ImageIcon resultIcon = new ImageIcon(image);
        return resultIcon;
    }

    public InteractiveIcon mirrorHorizontal() {
        defaultIcon = mirrorHorizontal(defaultIcon);
        clearRolloverPressedIcons();
        return this;
    }
    
    public InteractiveIcon mirrorVertical() {
        defaultIcon = mirrorVertical(defaultIcon);
        clearRolloverPressedIcons();
        return this;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        defaultIcon.paintIcon(c, g, x, y);
    }

    @Override
    public int getIconWidth() {
        return defaultIcon.getIconWidth();
    }

    @Override
    public int getIconHeight() {
        return defaultIcon.getIconHeight();
    }

    private static BufferedImage convertImageToBufferedImage(ImageIcon icon) {
        BufferedImage newImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics gr = newImage.getGraphics();
        gr.drawImage(icon.getImage(), 0, 0, null);
        gr.dispose();
        return newImage;
    }

    private static ImageIcon createRolloverIcon(ImageIcon icon) {
        BufferedImage image = convertImageToBufferedImage(icon);
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) result.getGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        int[] buf = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();
        int w = result.getWidth();
        int h = result.getHeight();
        int pixelIndex = 0;
        int increase = 20;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++, pixelIndex++) {
                int a = 0;
                int r = 0;
                int g = 0;
                int b = 0;
                int v = buf[pixelIndex];
                b = v & 0xFF;
                g = (v >> 8) & 0xFF;
                r = (v >> 16) & 0xFF;
                a = (v >> 24) & 0xFF;
                if (a != 0) {
                    r += increase;
                    g += increase;
                    b += increase;
                    if (r > 255) {
                        r = 255;
                    }
                    if (g > 255) {
                        g = 255;
                    }
                    if (b > 255) {
                        b = 255;
                    }
                    v = a;
                    v = v << 8;
                    v |= r;
                    v = v << 8;
                    v |= g;
                    v = v << 8;
                    v |= b;
                    buf[pixelIndex] = v;
                }
            }
        }

        ImageIcon resultIcon = new ImageIcon(result);
        return resultIcon;
    }

    private static ImageIcon createPressedIcon(ImageIcon icon) {
        BufferedImage image = convertImageToBufferedImage(icon);
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g2d = (Graphics2D) result.getGraphics();
        g2d.drawImage(image, 1, 1, image.getWidth() - 2, image.getHeight() - 2, null);
        g2d.dispose();
        return new ImageIcon(result);
    }
}