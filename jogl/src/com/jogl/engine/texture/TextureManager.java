package com.jogl.engine.texture;

import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.TextureIO;
import com.jogl.Utils;
import com.jogl.engine.SceneManager;
import java.awt.image.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;

/**
 * @author Dmitry
 */
public class TextureManager {
    private final Map<File, Texture> mapTexture = new HashMap<>();
    private SceneManager sceneManager;

    public TextureManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public void setSceneManager(SceneManager sceneManager) {
        this.sceneManager = sceneManager;
    }

    public Texture loadTexture(File file) throws IOException {
        Texture texture = mapTexture.get(file);
        if (texture == null) {
            int buffer = readTexture(sceneManager.getGl(), file);
            texture = new Texture();
            texture.setTextureId(buffer);
            texture.setFile(file);
            mapTexture.put(file, texture);
        }
        return texture;
    }

    private BufferedImage readImage(File file) throws IOException {
        System.out.println("Try to load texture " + file.getAbsolutePath());
        String extension = FilenameUtils.getExtension(file.getName().toLowerCase());
        switch (extension) {
            case "jpg":
            case "bmp":
            case "png":
            case "gif":
                BufferedImage image = ImageIO.read(file);
                return image;
            case "tga":
                byte[] array = new byte[(int) file.length()];
                try (InputStream stream = new FileInputStream(file)) {
                    stream.read(array);
                }
                return Utils.createTGAImage(array);
        }
        throw new RuntimeException("Cannot read image " + file.getName());
    }

    private int readTexture(GL3 gl, File file) throws IOException {
        com.jogamp.opengl.util.texture.Texture texture = TextureIO.newTexture(file, true);
        return texture.getTextureObject();

        /*
        
        
         BufferedImage image = readImage(file);
         if (image.getRaster().getDataBuffer() instanceof DataBufferByte) {
         byte[] buffer = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
         IntBuffer tTextureID = IntBuffer.allocate(1);
         gl.glGenTextures(1, tTextureID);
         gl.glBindTexture(GL_TEXTURE_2D, tTextureID.get(0));
         gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, image.getWidth(), image.getHeight(), 0, GL_BGR, GL_UNSIGNED_BYTE, ByteBuffer.wrap(buffer));
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
         gl.glGenerateMipmap(GL_TEXTURE_2D);
         return tTextureID;
         } else {
         int[] buffer = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
         IntBuffer tTextureID = IntBuffer.allocate(1);
         gl.glGenTextures(1, tTextureID);
         gl.glBindTexture(GL_TEXTURE_2D, tTextureID.get(0));
         gl.glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, image.getWidth(), image.getHeight(), 0, GL_BGR, GL_UNSIGNED_BYTE, IntBuffer.wrap(buffer));
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
         gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
         gl.glGenerateMipmap(GL_TEXTURE_2D);
         return tTextureID;
         }*/
    }
}
