package com.jogl.unpack;

import java.util.List;

/**
 * @author Dmitry
 */
public class PackPattern {
    private final List<BasePacker> packers;

    public PackPattern(List<BasePacker> packers) {
        this.packers = packers;
    }

    List<BasePacker> getPackers() {
        return packers;
    }

}
