package com.nwn.data.tpc;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dmitry
 */
public class TpcTexture {
    private String name;
    private int width;
    private int height;
    private int layersCount;
    private TpcEncoding encoding;
    private TpcPixelFormat format;
    private TpcPixelFormatRaw rawFormat;
    private PixelDataType dataType;
    private boolean hasAlpha;
    private boolean compressed;
    private int minDataSize;
    private int mipMapCount;
    private boolean isCubeMap;
    private int fileSize;
    private final List<TpcMipMap> mipMaps = new ArrayList<>();
    private String originalFormat;

    public TpcTexture() {
        layersCount = 1;
    }

    public void setOriginalFormat(String originalFormat) {
        this.originalFormat = originalFormat;
    }

    public String getOriginalFormat() {
        return originalFormat;
    }

    public void setEncoding(TpcEncoding encoding) {
        this.encoding = encoding;
    }

    public TpcEncoding getEncoding() {
        return encoding;
    }

    public List<TpcMipMap> getMipMaps() {
        return mipMaps;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getFileSize() {
        return fileSize;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLayersCount() {
        return layersCount;
    }

    public void setLayersCount(int layersCount) {
        this.layersCount = layersCount;
    }

    public TpcPixelFormat getFormat() {
        return format;
    }

    public void setFormat(TpcPixelFormat format) {
        this.format = format;
    }

    public TpcPixelFormatRaw getRawFormat() {
        return rawFormat;
    }

    public void setRawFormat(TpcPixelFormatRaw rawFormat) {
        this.rawFormat = rawFormat;
    }

    public PixelDataType getDataType() {
        return dataType;
    }

    public void setDataType(PixelDataType dataType) {
        this.dataType = dataType;
    }

    public boolean isHasAlpha() {
        return hasAlpha;
    }

    public void setHasAlpha(boolean hasAlpha) {
        this.hasAlpha = hasAlpha;
    }

    public boolean isCompressed() {
        return compressed;
    }

    public void setCompressed(boolean compressed) {
        this.compressed = compressed;
    }

    public int getMinDataSize() {
        return minDataSize;
    }

    public void setMinDataSize(int minDataSize) {
        this.minDataSize = minDataSize;
    }

    public int getMipMapCount() {
        return mipMapCount;
    }

    public void setMipMapCount(int mipMapCount) {
        this.mipMapCount = mipMapCount;
    }

    public boolean isIsCubeMap() {
        return isCubeMap;
    }

    public void setIsCubeMap(boolean isCubeMap) {
        this.isCubeMap = isCubeMap;
    }

}
