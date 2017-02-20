package com.utils;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import java.awt.Color;

public class ColorSerializer extends Serializer<Color> {
    public void write(Kryo kryo, Output output, Color object) {
        output.writeInt(object.getRGB());
    }

    public Color read(Kryo kryo, Input input, Class<Color> type) {
        return new Color(input.readInt(), true);
    }
}
