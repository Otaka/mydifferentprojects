package com.nwn.data;

import com.nwn.data.tpc.*;
import com.nwn.exceptions.ParsingException;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author Dmitry<br/> Based on the xoreos source code
 */
public class FileReaderTpc extends BaseReader {

    public TpcTexture loadFile(FileInputStream stream, int size, String fileName) throws IOException {
        init(stream);
        TpcTexture tpc = new TpcTexture();
        tpc.setName(fileName);
        int fileSize = (int) stream.getChannel().size();
        tpc.setFileSize(fileSize);
        readHeader(tpc, stream);
        readData(tpc, stream);
        readTxi(tpc, size, stream);
        verticalMirror(tpc);
        decompress(tpc);
        convertToRGBA(tpc);
        return tpc;
    }

    private void convertToRGBA(TpcTexture texture) {
        new TpcDecompress().convert2RGBA(texture);
    }

    private void decompress(TpcTexture texture) throws IOException {
        if (!texture.isCompressed()) {
            return;
        }
        TpcDecompress decompresser = new TpcDecompress();
        decompresser.decompress(texture);
    }

    private void verticalMirror(TpcTexture texture) throws IOException {
        if (texture.isCompressed()) {
            return;
        }
        if (texture.getFormat() == TpcPixelFormat.RGBA || texture.getFormat() == TpcPixelFormat.BGRA) {
            for (TpcMipMap mipMap : texture.getMipMaps()) {
                TpcDecompress.verticalMirror(mipMap, 4);
            }
        }
    }

    private void readData(TpcTexture tpc, FileInputStream stream) throws IOException {
        for (TpcMipMap mipMap : tpc.getMipMaps()) {
            int width = mipMap.getWidth();
            int height = mipMap.getHeight();
            TpcEncoding encoding = tpc.getEncoding();
// If the texture width is a power of two, the texture memory layout is "swizzled"
            boolean widthPot = (width & (width - 1)) == 0;
            boolean swizzled = encoding == TpcEncoding.SWIZZLED_BGRA && widthPot;

            mipMap.setData(new byte[mipMap.getSize()]);

            if (swizzled) {
                byte[] tmp = new byte[mipMap.getSize()];
                stream.read(tmp);
                deswizzle(mipMap, tmp);
            } else {
                stream.read(mipMap.getData());
            }
        }
    }

    private void readTxi(TpcTexture tpc, int size, FileInputStream stream) throws IOException {
        int txiDataSize = (int) (size - getPosition(stream));
        if (txiDataSize == 0) {
            return;
        }
        try {
            TxiReader txiReader = new TxiReader(stream, size);
            Txi txi = txiReader.load();
        } catch (Exception ex) {
            System.err.println("Cannot read txi in texture [" + tpc.getName() + "]");
            ex.printStackTrace();
        }
    }

    private void readHeader(TpcTexture tpc, FileInputStream stream) throws IOException {
        int dataSize = readInt(stream);
        stream.skip(4);
        int width = readShort(stream);
        int height = readShort(stream);
        tpc.setWidth(width);
        tpc.setHeight(height);
        if (width > 0x8000 || height > 0x8000 || width < 0 || height < 0) {
            throw new IOException("Tpc file " + tpc.getName() + " has wrong dimensions [" + width + "," + height + "]");
        }
        int encodingByte = readByte(stream);
        TpcEncoding encoding = TpcEncoding.fromValue(encodingByte);
        int mipMapCount = readByte(stream);
        skip(stream, 114);
        tpc.setMipMapCount(mipMapCount);
        if (dataSize == 0) {//Uncompressed
            tpc.setCompressed(false);
            switch (encoding) {
                case GRAY:
                    tpc.setHasAlpha(false);
                    tpc.setFormat(TpcPixelFormat.GRAY);
                    tpc.setRawFormat(TpcPixelFormatRaw.GRAY);
                    tpc.setDataType(PixelDataType.PixelDataType8);
                    tpc.setMinDataSize(1);
                    tpc.setOriginalFormat("GRAY");
                    dataSize = width * height * tpc.getMinDataSize();
                    break;
                case RGB:
                    //RGB no alpha channel
                    tpc.setHasAlpha(false);
                    tpc.setFormat(TpcPixelFormat.RGB);
                    tpc.setRawFormat(TpcPixelFormatRaw.RGB8);
                    tpc.setDataType(PixelDataType.PixelDataType8);
                    tpc.setMinDataSize(3);
                    tpc.setOriginalFormat("RGB");
                    dataSize = width * height * tpc.getMinDataSize();
                    break;
                case RGBA:
                    // RGBA, alpha channel
                    tpc.setHasAlpha(true);
                    tpc.setFormat(TpcPixelFormat.RGBA);
                    tpc.setRawFormat(TpcPixelFormatRaw.RGBA8);
                    tpc.setDataType(PixelDataType.PixelDataType8);
                    tpc.setMinDataSize(4);
                    tpc.setOriginalFormat("RGBA");
                    dataSize = width * height * tpc.getMinDataSize();
                    break;
                case SWIZZLED_BGRA:
                    tpc.setHasAlpha(true);
                    tpc.setFormat(TpcPixelFormat.BGRA);
                    tpc.setRawFormat(TpcPixelFormatRaw.RGBA8);
                    tpc.setDataType(PixelDataType.PixelDataType8);
                    tpc.setMinDataSize(4);
                    tpc.setOriginalFormat("SWIZZLED_RGBA");
                    dataSize = width * height * tpc.getMinDataSize();
                    break;
                default:
                    throw new ParsingException("TPC encoding " + encoding + " currently is not supported");
            }
        } else if (encoding == TpcEncoding.RGB) {
            // S3TC DXT1
            tpc.setCompressed(true);
            tpc.setHasAlpha(false);
            tpc.setFormat(TpcPixelFormat.BGR);
            tpc.setRawFormat(TpcPixelFormatRaw.DXT1);
            tpc.setDataType(PixelDataType.PixelDataType8);
            tpc.setOriginalFormat("DXT1");
            tpc.setMinDataSize(8);
            checkCubeMap(tpc);
        } else if (encoding == TpcEncoding.RGBA) {
            // S3TC DXT5
            tpc.setCompressed(true);
            tpc.setHasAlpha(true);
            tpc.setFormat(TpcPixelFormat.BGRA);
            tpc.setRawFormat(TpcPixelFormatRaw.DXT5);
            tpc.setDataType(PixelDataType.PixelDataType8);
            tpc.setOriginalFormat("DXT5");
            tpc.setMinDataSize(16);
            checkCubeMap(tpc);
        } else {
            throw new RuntimeException("Wrong encoding " + encoding);
        }

        final int fullImageDataSize = getDataSize(tpc, width, height);
        int fullDataSize = tpc.getFileSize() - 128;
        if (fullDataSize < (tpc.getLayersCount() * fullImageDataSize)) {
            throw new IOException("Wrong texture [" + tpc.getName() + "]. Image would not fit into data");
        }

        int layerCount;
        for (layerCount = 0; layerCount < tpc.getLayersCount(); layerCount++) {
            int layerWidth = width;
            int layerHeight = tpc.getHeight();
            int layerSize = dataSize;
            for (int i = 0; i < mipMapCount; i++) {
                TpcMipMap mipMap = new TpcMipMap();
                mipMap.setHeight(Math.max(1, layerHeight));
                mipMap.setWidth(Math.max(1, layerWidth));
                mipMap.setSize(Math.max(layerSize, tpc.getMinDataSize()));

                fullDataSize -= mipMap.getSize();
                tpc.getMipMaps().add(mipMap);
                layerWidth = layerWidth >> 1;
                layerHeight = layerHeight >> 1;
                layerSize = layerSize >> 2;
                if (layerWidth < 1 && layerHeight < 1) {
                    break;
                }
            }
        }

        if ((layerCount != tpc.getLayersCount()) || ((tpc.getMipMaps().size() % tpc.getLayersCount()) != 0)) {
            throw new IOException("Failed to correctly read all texture layers");
        }
    }

    private int getDataSize(TpcTexture texture, int width, int height) throws IOException {
        if (width < 0 || height < 0 || width > 0x8000 || height > 0x8000) {
            throw new IOException("Invalid dimensions [" + width + "," + height + "] in texture " + texture.getName());
        }
        switch (texture.getRawFormat()) {
            case GRAY:
                return width * height * 1;
            case RGB8:
                return width * height * 3;
            case RGBA8:
                return width * height * 4;
            case RGB5A1:
            case RGB5:
                return width * height * 2;
            case DXT1:
                return Math.max(8, ((width + 3) / 4) * ((height + 3) / 4) * 8);
            case DXT3:
            case DXT5:
                return Math.max(16, ((width + 3) / 4) * ((height + 3) / 4) * 16);

        }
        throw new IOException("Invalid data format " + texture.getRawFormat());
    }

    private void deswizzle(TpcMipMap mipMap, byte[] src) {
        int height = mipMap.getHeight();
        int width = mipMap.getWidth();
        byte[] dst = mipMap.getData();
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

    private boolean checkCubeMap(TpcTexture texture) {
        int height = texture.getHeight();
        int width = texture.getWidth();
        if ((height == 0) || (width == 0) || ((height / width) != 6)) {
            return false;
        }

        texture.setHeight(height / 6);

        texture.setLayersCount(6);
        texture.setIsCubeMap(true);
        return true;
    }
}
