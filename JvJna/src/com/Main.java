/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.GDI32;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HBITMAP;
import com.sun.jna.platform.win32.WinDef.HDC;
import com.sun.jna.platform.win32.WinDef.HWND;
import com.sun.jna.platform.win32.WinDef.RECT;
import com.sun.jna.platform.win32.WinGDI;
import com.sun.jna.platform.win32.WinNT;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author sad
 */
public class Main {

    public static BufferedImage getScreenshot(long hdc) {
        HWND hwnd = new HWND(new Pointer(hdc));
        HDC windowDC = User32.INSTANCE.GetDC(hwnd);
        RECT rect = new WinDef.RECT();
        USER.GetWindowRect(hwnd, rect);
        int width = rect.right - rect.left;
        int height = rect.bottom - rect.top;
        HBITMAP outputBitmap
                = GDI32.INSTANCE.CreateCompatibleBitmap(windowDC,
                        width, height);
        try {
            WinDef.HDC blitDC = GDI32.INSTANCE.CreateCompatibleDC(windowDC);
            try {
                WinNT.HANDLE oldBitmap
                        = GDI32.INSTANCE.SelectObject(blitDC, outputBitmap);
                try {

                    GdiBlit.INSTANCE.BitBlt(blitDC, 0, 0, width, height, windowDC, 0, 0, GdiBlit.SRCCOPY);
                } finally {
                    GDI32.INSTANCE.SelectObject(blitDC, oldBitmap);
                }
                WinGDI.BITMAPINFO bi = new WinGDI.BITMAPINFO(40);
                bi.bmiHeader.biSize = 40;
                int ok = GDI32.INSTANCE.GetDIBits(blitDC, outputBitmap, 0, height,
                        null, bi, WinGDI.DIB_RGB_COLORS);
                if (ok != 0) {
                    WinGDI.BITMAPINFOHEADER bih = bi.bmiHeader;
                    bih.biHeight = -Math.abs(bih.biHeight);
                    bi.bmiHeader.biCompression = 0;
                    return bufferedImageFromBitmap(blitDC, outputBitmap, bi);
                } else {
                    return null;
                }
            } finally {
                GDI32.INSTANCE.DeleteObject(blitDC);
            }
        } finally {
            GDI32.INSTANCE.DeleteObject(outputBitmap);
        }
    }

    private static BufferedImage bufferedImageFromBitmap(WinDef.HDC blitDC,
            WinDef.HBITMAP outputBitmap,
            WinGDI.BITMAPINFO bi) {
        WinGDI.BITMAPINFOHEADER bih = bi.bmiHeader;
        int height = Math.abs(bih.biHeight);
        final ColorModel cm;
        final DataBuffer buffer;
        final WritableRaster raster;
        int strideBits
                = (bih.biWidth * bih.biBitCount);
        int strideBytesAligned
                = (((strideBits - 1) | 0x1F) + 1) >> 3;
        final int strideElementsAligned;
        switch (bih.biBitCount) {
            case 16:
                strideElementsAligned = strideBytesAligned / 2;
                cm = new DirectColorModel(16, 0x7C00, 0x3E0, 0x1F);
                buffer
                        = new DataBufferUShort(strideElementsAligned * height);
                raster
                        = Raster.createPackedRaster(buffer,
                                bih.biWidth, height,
                                strideElementsAligned,
                                ((DirectColorModel) cm).getMasks(),
                                null);
                break;
            case 32:
                strideElementsAligned = strideBytesAligned / 4;
                cm = new DirectColorModel(32, 0xFF0000, 0xFF00, 0xFF);
                buffer = new DataBufferInt(strideElementsAligned * height);
                raster
                        = Raster.createPackedRaster(buffer,
                                bih.biWidth, height,
                                strideElementsAligned,
                                ((DirectColorModel) cm).getMasks(),
                                null);
                break;
            default:
                throw new IllegalArgumentException("Unsupported bit count: " + bih.biBitCount);
        }
        final int ok;
        switch (buffer.getDataType()) {
            case DataBuffer.TYPE_INT: {
                int[] pixels = ((DataBufferInt) buffer).getData();
                Memory mem = new Memory(pixels.length * 4);
                ok = GDI.GetDIBits(blitDC, outputBitmap, 0, raster.getHeight(), mem, bi, 0);
                for (int i = 0; i < pixels.length; i++) {
                    pixels[i] = mem.getInt(i * 4);
                }
            }
            break;
            case DataBuffer.TYPE_USHORT: {
                short[] pixels = ((DataBufferUShort) buffer).getData();
                Memory mem = new Memory(pixels.length * 2);
                ok = GDI.GetDIBits(blitDC, outputBitmap, 0, raster.getHeight(), mem, bi, 0);
                for (int i = 0; i < pixels.length; i++) {
                    pixels[i] = mem.getShort(i);
                }
            }
            break;
            default:
                throw new AssertionError("Unexpected buffer element type: " + buffer.getDataType());
        }
        // if (ok) {
        return new BufferedImage(cm, raster, false, null);
        // } else {
        //       return null;
        // }
    }
    private static final User32 USER = User32.INSTANCE;
    private static final GDI32 GDI = GDI32.INSTANCE;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Start");        
        long chromeHwnd = 0x002306EA;
        BufferedImage image = getScreenshot(chromeHwnd);
        
        Color c=new Color(253, 135, 4);
        int rgb=c.getRGB();
        //BufferedImage image = ImageIO.read(new File("d:/Screenshot_1.png"));
        for (int j = 0; j < image.getHeight(); j++) {
            for (int i = 0; i < image.getWidth(); i++) {
                int color = image.getRGB(i, j);
                if (color == rgb) {
                    image.setRGB(i, j, 0xffffff);
                }
            }
        }
        ImageIO.write(image, "png", new File("d:/2.png"));
    }

}
