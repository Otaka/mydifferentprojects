package com.gooddies.utils;

import java.util.Iterator;

/**
 * @author sad
 */
public class IteratorToIterable {

    public static <T> Iterable<T> once(final Iterator<T> source) {
        return new Iterable<T>() {
            @Override
            public Iterator<T> iterator() {
                return source;
            }
        };
    }
}
