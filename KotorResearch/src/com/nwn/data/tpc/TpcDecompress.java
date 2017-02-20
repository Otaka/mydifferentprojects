package com.nwn.data.tpc;

import com.nwn.data.BaseReader;
import java.io.*;
import java.util.Arrays;

/**
 * @author Dmitry. Based on xoreos source code
 */
public class TpcDecompress extends BaseReader {
    public void convert2RGBA(TpcTexture texture) {
        if (texture.isCompressed()) {
            return;
        }

        for (TpcMipMap mipMap : texture.getMipMaps()) {
            if (texture.getFormat() == TpcPixelFormat.RGB) {
                convertFromRGB2RGBA(mipMap);
                texture.setDataType(PixelDataType.PixelDataType8);
                texture.setFormat(TpcPixelFormat.RGBA);
                texture.setRawFormat(TpcPixelFormatRaw.RGBA8);
                texture.setMinDataSize(4);
            } else if (texture.getFormat() == TpcPixelFormat.BGR) {
                convertFromBgr2RGBA(mipMap);
                texture.setDataType(PixelDataType.PixelDataType8);
                texture.setFormat(TpcPixelFormat.RGBA);
                texture.setRawFormat(TpcPixelFormatRaw.RGBA8);
                texture.setMinDataSize(4);
            } else if (texture.getFormat() == TpcPixelFormat.GRAY) {
                convertFromGRAY2RGBA(mipMap);
                texture.setDataType(PixelDataType.PixelDataType8);
                texture.setFormat(TpcPixelFormat.RGBA);
                texture.setRawFormat(TpcPixelFormatRaw.RGBA8);
                texture.setMinDataSize(4);
            }
        }
    }

    private void convertFromGRAY2RGBA(TpcMipMap mipMap) {
        byte[] src = mipMap.getData();
        byte[] dest = new byte[mipMap.getWidth() * mipMap.getHeight() * 4];
        int destIndex = 0;
        for (int i = 0; i < src.length; i++, destIndex += 4) {
            dest[destIndex + 0] = src[i];
            dest[destIndex + 1] = src[i];
            dest[destIndex + 2] = src[i];
            dest[destIndex + 3] = -1;
        }

        mipMap.setData(dest);
        mipMap.setSize(dest.length);
        verticalMirror(mipMap, 4);
    }

    private void convertFromRGB2RGBA(TpcMipMap mipMap) {
        byte[] src = mipMap.getData();
        byte[] dest = new byte[mipMap.getWidth() * mipMap.getHeight() * 4];
        int destIndex = 0;
        for (int i = 0; i < src.length; i += 3, destIndex += 4) {
            dest[destIndex + 0] = src[i + 0];
            dest[destIndex + 1] = src[i + 1];
            dest[destIndex + 2] = src[i + 2];
            dest[destIndex + 3] = -1;
        }

        mipMap.setData(dest);
        mipMap.setSize(dest.length);
        verticalMirror(mipMap, 4);
    }

    private void convertFromBgr2RGBA(TpcMipMap mipMap) {
        byte[] src = mipMap.getData();
        byte[] dest = new byte[mipMap.getWidth() * mipMap.getHeight() * 4];
        int destIndex = 0;
        for (int i = 0; i < dest.length; i += 3, destIndex += 4) {
            dest[destIndex + 0] = src[i + 2];
            dest[destIndex + 1] = src[i + 1];
            dest[destIndex + 2] = src[i + 0];
            dest[destIndex + 3] = -1;
        }

        mipMap.setData(dest);
        mipMap.setSize(dest.length);
        verticalMirror(mipMap, 4);
    }

    public static void verticalMirror(TpcTexture texture, int recordSize) {
        for (TpcMipMap mipMap : texture.getMipMaps()) {
            verticalMirror(mipMap, recordSize);
        }
    }

    public static void verticalMirror(TpcMipMap mipMap, int recordSize) {
        byte[] dest = mipMap.getData();
        int halfOfHeight = mipMap.getHeight() / 2;
        int pitch = mipMap.getWidth() * recordSize;
        for (int j = 0; j < halfOfHeight; j++) {
            int index1 = j * pitch;
            int index2 = (mipMap.getHeight() - j - 1) * pitch;
            for (int i = 0; i < pitch; i++, index1++, index2++) {

                byte t = dest[index1];
                dest[index1] = dest[index2];
                dest[index2] = t;
            }
        }
    }

    public void decompress(TpcTexture texture) throws IOException {
        if (!texture.isCompressed()) {
            return;
        }
        if (!(texture.getRawFormat() == TpcPixelFormatRaw.DXT1 || texture.getRawFormat() == TpcPixelFormatRaw.DXT3 || texture.getRawFormat() == TpcPixelFormatRaw.DXT5)) {
            throw new IllegalArgumentException("Bad compression format " + texture.getRawFormat() + " in texture [" + texture.getName() + "]");
        }

        for (TpcMipMap mipMap : texture.getMipMaps()) {
            ByteArrayInputStream src = new ByteArrayInputStream(Arrays.copyOf(mipMap.getData(), mipMap.getData().length));
            /* The DXT algorithms work on 4x4 pixel blocks. Textures smaller than one
             * block will be padded, but larger textures need to be correctly aligned. */
            byte[] result;
            if (texture.getRawFormat() == TpcPixelFormatRaw.DXT1) {
                result = decompressDxt1(texture, src);
                mipMap.setData(result);
                mipMap.setSize(mipMap.getWidth() * mipMap.getHeight() * 4);
            } else if (texture.getRawFormat() == TpcPixelFormatRaw.DXT5) {
                result = decompressDxt5(texture, src);
                mipMap.setData(result);
                mipMap.setSize(mipMap.getWidth() * mipMap.getHeight() * 4);
                verticalMirror(mipMap, 4);
            } else if (texture.getRawFormat() == TpcPixelFormatRaw.DXT3) {
                throw new RuntimeException("DXT3 uncompression is not implemented yet");
            } else {
                throw new RuntimeException("Unknown compression");
            }

        }

        texture.setCompressed(false);
        texture.setDataType(PixelDataType.PixelDataType8);
        texture.setFormat(TpcPixelFormat.RGBA);
        texture.setRawFormat(TpcPixelFormatRaw.RGBA8);
        texture.setMinDataSize(4);
    }

    private final int[] blended = new int[4];
    private final int[] alphab = new int[8];

    private long read48LE(ByteArrayInputStream src) throws IOException {
        long output = readInt(src) & 0xffffffffl;
        long second = readShort(src) & 0xffff;
        return output | (second << 32);
    }

    private int little2big(int i) {
        return (i & 0xff) << 24 | (i & 0xff00) << 8 | (i & 0xff0000) >> 8 | (i >> 24) & 0xff;
    }

    private byte[] decompressDxt5(TpcTexture texture, ByteArrayInputStream src) throws IOException {
        int width = texture.getWidth();
        int height = texture.getHeight();
        int size = width * height * 4;
        int pitch = width * 4;
        byte[] dest = new byte[size];
        for (int ty = height; ty > 0; ty -= 4) {
            //for (int ty = 3; ty < height; ty += 4) {
            for (int tx = 0; tx < width; tx += 4) {
                int alpha0 = readByte(src);
                int alpha1 = readByte(src);
                long alphabl = read48LE(src);
                int color0 = readShort(src) & 0xffff;
                int color1 = readShort(src) & 0xffff;
                long colors = little2big(readInt(src)) & 0xffffffffl;
                alphab[0] = alpha0;
                alphab[1] = alpha1;

                if (alpha0 > alpha1) {
                    alphab[2] = (byte) ((6.0f * (double) alphab[0] + 1.0f * (double) alphab[1] + 3.0f) / 7.0f);
                    alphab[3] = (byte) ((5.0f * (double) alphab[0] + 2.0f * (double) alphab[1] + 3.0f) / 7.0f);
                    alphab[4] = (byte) ((4.0f * (double) alphab[0] + 3.0f * (double) alphab[1] + 3.0f) / 7.0f);
                    alphab[5] = (byte) ((3.0f * (double) alphab[0] + 4.0f * (double) alphab[1] + 3.0f) / 7.0f);
                    alphab[6] = (byte) ((2.0f * (double) alphab[0] + 5.0f * (double) alphab[1] + 3.0f) / 7.0f);
                    alphab[7] = (byte) ((1.0f * (double) alphab[0] + 6.0f * (double) alphab[1] + 3.0f) / 7.0f);
                } else {
                    alphab[2] = (byte) ((4.0f * (double) alphab[0] + 1.0f * (double) alphab[1] + 2.0f) / 5.0f);
                    alphab[3] = (byte) ((3.0f * (double) alphab[0] + 2.0f * (double) alphab[1] + 2.0f) / 5.0f);
                    alphab[4] = (byte) ((2.0f * (double) alphab[0] + 3.0f * (double) alphab[1] + 2.0f) / 5.0f);
                    alphab[5] = (byte) ((1.0f * (double) alphab[0] + 4.0f * (double) alphab[1] + 2.0f) / 5.0f);
                    alphab[6] = 0;
                    alphab[7] = 255;
                }

                blended[0] = convert565To8888(color0) & 0xFFFFFF00;
                blended[1] = convert565To8888(color1) & 0xFFFFFF00;
                blended[2] = interpolate32(0.333333f, blended[0], blended[1]);
                blended[3] = interpolate32(0.666666f, blended[0], blended[1]);
                long cpx = colors;
                int blockWidth = Math.min(width, 4);
                int blockHeight = Math.min(height, 4);
                for (byte y = 0; y < blockHeight; ++y) {
                    for (byte x = 0; x < blockWidth; ++x) {
                        int destX = tx + x;
                        int destY = height - 1 - (ty - blockHeight + y);
                        int alpha = alphab[(int) ((alphabl >> (3 * (4 * (3 - y) + x))) & 7)] & 0xff;
                        long pixel = (blended[(int) (cpx & 3)] | alpha) & 0xffffffffl;
                        cpx >>= 2;
                        if ((destX < width) && (destY < height)) {
                            writeBeUInt32(dest, (int) pixel, destY * pitch + destX * 4);
                        }
                    }
                }
            }
        }

        return dest;
    }

    private byte[] decompressDxt1(TpcTexture texture, ByteArrayInputStream src) throws IOException {
        int width = texture.getWidth();
        int height = texture.getHeight();
        int size = width * height * 4;
        int pitch = width * 4;
        byte[] dest = new byte[size];
        for (int ty = 3; ty < height; ty += 4) {
            for (int tx = 0; tx < width; tx += 4) {
                int color0 = readShort(src) & 0xffff;
                int color1 = readShort(src) & 0xffff;
                int colors = readInt(src);
                blended[0] = convert565To8888(color0);
                blended[1] = convert565To8888(color1);
                if (color0 > color1) {
                    blended[2] = interpolate32(0.33333333333333333333f, blended[0], blended[1]);
                    blended[3] = interpolate32(0.66666666666666666666f, blended[0], blended[1]);
                } else {
                    blended[2] = interpolate32(0.5f, blended[0], blended[1]);
                    blended[3] = 0;
                }

                int cpx = colors;
                int blockWidth = Math.min(width, 4);
                int blockHeight = Math.min(height, 4);
                for (int y = 0; y < blockHeight; ++y) {
                    for (int x = 0; x < blockWidth; ++x) {
                        int destX = tx + x;
                        int destY = height - 1 - (ty - blockHeight + y);
                        int pixel = blended[cpx & 3];
                        //  System.out.println(destY);
                        cpx >>= 2;
                        if ((destX < width) && (destY < height)) {
                            int offset = destY * pitch + destX * 4;
                            writeBeUInt32(dest, pixel, offset);
                        }
                    }
                }
            }
        }

        return dest;
    }

    protected static void writeBeUInt32(byte[] b, int value, int offset) {
        b[0 + offset] = (byte) ((value & 0xffffffffl) >> 24);
        b[1 + offset] = (byte) ((value & 0xffffffffl) >> 16);
        b[2 + offset] = (byte) ((value & 0xffffffffl) >> 8);
        b[3 + offset] = (byte) (value & 0xffffffffl);
    }

    protected static int convert565To8888(int color) {
        return ((color & 0x1F) << 11) | ((color & 0x7E0) << 13) | ((color & 0xF800) << 16) | 0xFF;
    }

    protected static int interpolate32(double weight, int color_0, int color_1) {
        long color0Long = color_0 & 0xffffffffl;
        long color1Long = color_1 & 0xffffffffl;
        short r0, r1, r2, g0, g1, g2, b0, b1, b2, a0, a1, a2;
        r0 = (short) ((color0Long >>> 24) & 0xff);
        r1 = (short) ((color1Long >>> 24) & 0xff);
        r2 = (short) (((byte) ((1.0f - weight) * (double) r0 + weight * (double) r1)) & 0xff);
        g0 = (short) ((color_0 >>> 16) & 0xFF);
        g1 = (short) ((color_1 >>> 16) & 0xFF);
        g2 = (short) (((byte) ((1.0f - weight) * (double) g0 + weight * (double) g1)) & 0xff);
        b0 = (short) ((color_0 >>> 8) & 0xFF);
        b1 = (short) ((color_1 >>> 8) & 0xFF);
        b2 = (short) (((byte) ((1.0f - weight) * (double) b0 + weight * (double) b1)) & 0xff);
        a0 = (short) (color_0 & 0xFF);
        a1 = (short) (color_1 & 0xFF);
        a2 = (short) (((byte) ((1.0f - weight) * (double) a0 + weight * (double) a1)) & 0xff);
        long result = ((long) r2 << 24 | (long) g2 << 16 | (long) b2 << 8 | (long) a2);
        return (int) result;
    }
}
