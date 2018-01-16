package com.nwn.utils;

import com.nwn.data.tpc.TpcTexture;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * @author Dmitry
 */
public class TpcConverter {
    
    public BufferedImage convertToBufferedImage(TpcTexture texture){
    if (texture.getMipMaps().size() < 1) {
            throw new IllegalArgumentException("TpcTexture [" + texture.getName() + "] has empty mipmap list. Cannot create tga");
        }
        BufferedImage image = new BufferedImage(texture.getWidth(), texture.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        WritableRaster raster = image.getRaster();
        int lineByteCount = texture.getWidth() * texture.getMinDataSize();
        int[] array = new int[lineByteCount];
        byte[] srcBuffer = texture.getMipMaps().get(0).getData();
        for (int j = 0; j < texture.getHeight(); j++) {
            int offset = j * lineByteCount;
            for (int i = 0; i < lineByteCount; i++) {
                array[i] = srcBuffer[i + offset] & 0xff;
            }

            raster.setPixels(0, j, texture.getWidth(), 1, array);
        }
        return image;
    }
    
    public void saveTpcAsPng(TpcTexture texture, File outputFile) throws IOException {
        BufferedImage convertedTpc=convertToBufferedImage(texture);
        ImageIO.write(convertedTpc, "png", outputFile);
    }
    
    
}
