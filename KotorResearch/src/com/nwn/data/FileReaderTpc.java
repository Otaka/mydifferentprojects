package com.nwn.data;

import com.nwn.data.tpc.*;
import com.nwn.exceptions.ParsingException;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Dmitry<br/> Based on the xoreos source code
 */
public class FileReaderTpc extends BaseReader {
    private int layersCount = 1;

    public BufferedImage loadFile(FileInputStream stream, String fileName) throws IOException {
        init(stream);
        int fileSize = (int) stream.getChannel().size();
        int dataSize = readInt(stream);
        stream.skip(4);
        int width = readShort(stream);
        int height = readShort(stream);
        TpcEncoding encoding = TpcEncoding.fromValue(readByte(stream));
        int mipMapCount = readByte(stream);
        skip(stream, 114);
        int recordSize = 0;
        TpcPixelFormat format;
        TpcPixelFormatRaw rawFormat;
        boolean hasAlpha;
        boolean compressed;

        if (dataSize == 0) {//Uncompressed
            compressed = false;
            switch (encoding) {
                case GRAY:
                    hasAlpha = false;
                    format = TpcPixelFormat.RGB;
                    rawFormat = TpcPixelFormatRaw.RGB8;
                    recordSize = 1;
                    break;
                case RGB:
                    hasAlpha = false;
                    format = TpcPixelFormat.RGB;
                    rawFormat = TpcPixelFormatRaw.RGB8;
                    recordSize = 3;
                    break;
                case RGBA:
                    hasAlpha = true;
                    format = TpcPixelFormat.RGBA;
                    rawFormat = TpcPixelFormatRaw.RGBA8;
                    recordSize = 4;
                    break;
                case PACKED_BGRA:
                    hasAlpha = true;
                    format = TpcPixelFormat.BGRA;
                    rawFormat = TpcPixelFormatRaw.RGBA8;
                    recordSize = 4;
                    break;
                default:
                    throw new ParsingException("TPC encoding " + encoding + " currently is not supported");
            }
            dataSize = width * height * recordSize;
        } else if (encoding == TpcEncoding.RGB) {
            compressed = true;
            hasAlpha = false;
            format = TpcPixelFormat.BGR;
            rawFormat = TpcPixelFormatRaw.DXT1;
            recordSize = 8;
            checkCubeMap(width, height);
        } else if (encoding == TpcEncoding.RGBA) {
            compressed = true;
            hasAlpha = false;
            format = TpcPixelFormat.BGRA;
            rawFormat = TpcPixelFormatRaw.DXT5;
            recordSize = 16;
            checkCubeMap(width, height);
        } else {
            throw new RuntimeException("Wrong encoding " + encoding);
        }

        // If the texture width is a power of two, the texture memory layout is "swizzled"
        boolean widthPot = (width & (width - 1)) == 0;
        boolean packed = encoding == TpcEncoding.PACKED_BGRA && widthPot;
        int size = Math.max(dataSize, recordSize);
        byte[] data = new byte[size];
        if (packed) {
            byte[] tmp = new byte[size];
            stream.read(tmp);
            unpack(data, tmp, width, height);
        } else {
            stream.read(data);
            if (encoding == TpcEncoding.GRAY) {
                throw new UnsupportedOperationException("Currently grayscaled not packed images is not supported");
            }
        }

        int bufferedImageFormat;
        switch (format) {
            case BGR:
                bufferedImageFormat = BufferedImage.TYPE_3BYTE_BGR;
                break;
            case BGRA:
                bufferedImageFormat = BufferedImage.TYPE_4BYTE_ABGR;
                break;
            case RGB:
                bufferedImageFormat = BufferedImage.TYPE_INT_RGB;
                break;
            case RGBA:
                bufferedImageFormat = BufferedImage.TYPE_INT_ARGB;
                break;
            default:
                throw new RuntimeException("Wrong format");
        }
        BufferedImage image = new BufferedImage(width, height, bufferedImageFormat);
        WritableRaster raster = ((WritableRaster) image.getData());
        int[] dataArray = new int[recordSize];
        for (int y = 0; y < height; y++) {
            int offset = y * width * recordSize;
            for (int x = 0; x < width; x++) {
                for (int i = 0; i < recordSize; i++, offset++) {
                    dataArray[recordSize - i - 1] = data[offset] & 0xFF;
                }
                raster.setPixel(x, y, dataArray);
            }
        }
        return image;
    }

    private void unpack(byte[] dst, byte[] src, int width, int height) {
        int dstIndex = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int offset = deSwizzleOffset(x, y, width, height) * 4;
                dst[dstIndex] = src[offset + 0];
                dstIndex++;
                dst[dstIndex] = src[offset + 1];
                dstIndex++;
                dst[dstIndex] = src[offset + 2];
                dstIndex++;
                dst[dstIndex] = src[offset + 3];
                dstIndex++;
            }
        }
    }

    private static int log2(int n) {
        if (n <= 0) {
            throw new IllegalArgumentException();
        }
        return 31 - Integer.numberOfLeadingZeros(n);
    }

    private static int deSwizzleOffset(int x, int y, int width, int height) {
        width = log2(width);
        height = log2(height);

        int offset = 0;
        int shiftCount = 0;

        while ((width | height) != 0) {
            if (width != 0) {
                offset |= (x & 0x01) << shiftCount;

                x >>= 1;

                shiftCount++;
                width--;
            }

            if (height != 0) {
                offset |= (y & 0x01) << shiftCount;

                y >>= 1;

                shiftCount++;
                height--;
            }
        }

        return offset;
    }

    private boolean checkCubeMap(int width, int height) {
        if ((height / width) != 6) {
            return false;
        }

        height /= 6;
        layersCount = 6;
        throw new ParsingException("Cannot load cube maps yet");
        //return true;
    }
}
