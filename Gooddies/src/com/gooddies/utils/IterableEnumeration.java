package com.gooddies.utils;

import java.util.Enumeration;
import java.util.Iterator;

public class IterableEnumeration<T> implements Iterable<T> {
    private final Enumeration<T> en;

    private IterableEnumeration(Enumeration<T> en) {
        this.en = en;
    }

    // return an adaptor for the Enumeration
    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            @Override
            public boolean hasNext() {
                return en.hasMoreElements();
            }

            @Override
            public T next() {
                return en.nextElement();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public static <T> Iterable<T> make(Enumeration<T> en) {
        return new IterableEnumeration<T>(en);
    }
}
