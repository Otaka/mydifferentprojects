package com.drawables;

import com.gooddies.graphics.ExtRectangle;
import com.jsonparser.JsonObject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;
import javax.imageio.ImageIO;

/**
 * @author Dmitry
 */
public class ImageDrawable extends Drawable {

    private BufferedImage image;
    private String url;
    private boolean stretch = false;

    @Override
    public Drawable parse(JsonObject element) {
        super.parse(element);
        if (!element.has("url")) {
            throw new IllegalArgumentException("ImageDrawable should have 'url'");
        }
        if (element.has("stretch")) {
            stretch = element.getElementByName("stretch").getAsBoolean();
        }

        this.url = element.getElementByName("url").getAsString();
        try {
            URL url = new URL(this.url);
            url.getPath();
        } catch (MalformedURLException ex) {
            throw new IllegalArgumentException("Url '" + this.url + "' is not valid url");
        }

        return this;
    }

    private void drawTextUgly(String text, FontMetrics textMetrics, Graphics2D g2) {
        // Ugly code to wrap text
        int lineHeight = textMetrics.getHeight();
        String textToDraw = text;
        String[] arr = textToDraw.split(" ");
        int nIndex = 0;
        int startX = 0;
        int startY = 20;
        while (nIndex < arr.length) {
            String line = arr[nIndex++];
            while ((nIndex < arr.length) && (textMetrics.stringWidth(line + " " + arr[nIndex]) < 447)) {
                line = line + " " + arr[nIndex];
                nIndex++;
            }
            g2.drawString(line, startX, startY);
            startY = startY + lineHeight;
        }
    }

    private BufferedImage generateErrorImage(int width, int height, String text) {
        BufferedImage tImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = tImage.getGraphics();
        g.setColor(Color.WHITE);
        drawTextUgly(text, g.getFontMetrics(), (Graphics2D) g);

        g.dispose();
        return tImage;
    }

    private String getImageTypeFromMime(HttpURLConnection connection) {
        List<String> types = connection.getHeaderFields().get("Content-Type");
        if (types == null || types.isEmpty()) {
            return "jpeg";
        }
        String type = types.get(0).toLowerCase();
        if (type.equals("image/jpeg") || type.equals("image/jpg")) {
            return "jpeg";
        }
        if (type.equals("image/png")) {
            return "png";
        }
        if (type.equals("image/gif")) {
            return "gif";
        }
        return "jpeg";
    }

    public void loadImage(String url) {
        int urlHashCode = url.hashCode();
        File cacheFolder = new File("./cache");
        if (!cacheFolder.exists()) {
            cacheFolder.mkdir();
        }
        File cachedImageFile = new File(cacheFolder, "" + urlHashCode + ".pic");
        if (cachedImageFile.exists()) {
            try {
                image = ImageIO.read(cachedImageFile);
                return;
            } catch (IOException ex) {
                ex.printStackTrace();
                cachedImageFile.delete();
            }
        }

        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.connect();
            if (connection.getResponseCode() != 200) {
                Scanner scanner = new Scanner(connection.getErrorStream());
                image = generateErrorImage(500, 500, "Не найден ресурс по пути '" + url + "'\n" + scanner.nextLine());
                scanner.close();
            } else {
                try {
                    String imageType = getImageTypeFromMime(connection);
                    try (InputStream stream = connection.getInputStream()) {
                        image = ImageIO.read(stream);
                        ImageIO.write(image, imageType, cachedImageFile);
                    }
                } catch (Exception ex) {
                    image = generateErrorImage(500, 500, "Ошибка загрузки изображения URL='" + url + "' " + ex.getMessage());
                }
            }
        } catch (Exception ex) {
            image = generateErrorImage(500, 500, "Ошибка загрузки изображения URL='" + url + "' " + ex.getMessage());
        }
    }

    @Override
    public void paint(Graphics gr, int containerWidth, int containerHeight) {
        super.paint(gr, containerWidth, containerHeight);
        if (image == null) {
            loadImage(url);
        }
        ExtRectangle rect = new ExtRectangle(0, 0, image.getWidth(), image.getHeight());
        if (stretch) {
            rect.fitProportional(new ExtRectangle(0, 0, containerWidth, containerHeight));
        } else {
            if (image.getWidth() > containerWidth || image.getHeight() > containerHeight) {
                rect.fitProportional(new ExtRectangle(0, 0, containerWidth, containerHeight));
            }
        }
        gr.drawImage(image, (int) (containerWidth / 2 - rect.getWidth() / 2), (int) (containerHeight / 2 - rect.getHeight() / 2), (int) rect.getWidth(), (int) rect.getHeight(), null);
    }
}
