package com.jogl.unpack;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author Dmitry
 */
public class Unpack {
    private static final Map<String, PackPattern> patternsMap = new HashMap<>();

    public static List unpack(String pattern, InputStream stream) throws IOException {
        PackPattern pp = patternsMap.get(pattern);
        if (pp == null) {
            pp = new PatternCompiler().compilePattern(pattern);
            patternsMap.put(pattern, pp);
        }
        return unpack(stream, pp);
    }

    public static List<Object> unpack(InputStream stream, PackPattern packPattern) throws IOException {
        List<BasePacker> packers = packPattern.getPackers();
        List<Object> result = new ArrayList<>(packers.size());
        for (BasePacker packer : packers) {
            packer.process(stream, result);
        }
        return result;
    }
}
