package com.jogl;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL3;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.*;
import javax.imageio.ImageIO;

/**
 * @author Dmitry
 */
public class Utils {
    public static int loadShader(GL3 gl, String vertexShaderText, String fragmentShaderText) {
        int vertexShaderId = compileShader(gl, vertexShaderText, GL3.GL_VERTEX_SHADER);
        int fragmentShaderId = compileShader(gl, fragmentShaderText, GL3.GL_FRAGMENT_SHADER);
        int program = createShaderProgram(gl, vertexShaderId, fragmentShaderId);
        gl.glDeleteShader(vertexShaderId);
        gl.glDeleteShader(fragmentShaderId);
        return program;
    }

    public static IntBuffer createBuffer(GL3 gl, float[] buffer) {
        return createBuffer(gl, FloatBuffer.wrap(buffer));
    }

    public static IntBuffer createBuffer(GL3 gl, FloatBuffer buffer) {
        IntBuffer result = IntBuffer.allocate(1);
        gl.glGenBuffers(1, result);
        setBufferData(gl, result, buffer);
        return result;
    }

    public static void setBufferData(GL3 gl, IntBuffer bufferId, FloatBuffer data) {
        gl.glBindBuffer(GL3.GL_ARRAY_BUFFER, bufferId.get(0));
        gl.glBufferData(GL3.GL_ARRAY_BUFFER, data.limit() * 4, data, GL3.GL_STATIC_DRAW);
    }

    public static int createShaderProgram(GL3 gl, int vertexShader, int fragmentShader) {
        int program = gl.glCreateProgram();
        gl.glAttachShader(program, vertexShader);
        gl.glAttachShader(program, fragmentShader);
        gl.glLinkProgram(program);

        IntBuffer result = Buffers.newDirectIntBuffer(1);
        gl.glGetProgramiv(program, GL3.GL_LINK_STATUS, result);
        int resultCode = result.get(0);
        if (resultCode != 1) {
            IntBuffer infoLogLength = Buffers.newDirectIntBuffer(1);
            gl.glGetProgramiv(program, GL3.GL_INFO_LOG_LENGTH, infoLogLength);
            ByteBuffer logMessage = ByteBuffer.allocate(infoLogLength.get(0));
            gl.glGetProgramInfoLog(program, infoLogLength.get(0), result, logMessage);
            throw new RuntimeException("shader compiling error" + new String(logMessage.array()));
        }
        return program;
    }

    public static int compileShader(GL3 gl, String shaderText, int shaderType) {
        int shaderId = gl.glCreateShader(shaderType);
        gl.glShaderSource(shaderId, 1, new String[]{shaderText}, new int[]{shaderText.length()}, 0);
        gl.glCompileShader(shaderId);
        IntBuffer result = Buffers.newDirectIntBuffer(1);
        gl.glGetShaderiv(shaderId, GL3.GL_COMPILE_STATUS, result);
        int resultCode = result.get(0);
        if (resultCode != 1) {
            IntBuffer infoLogLength = Buffers.newDirectIntBuffer(1);
            gl.glGetShaderiv(shaderId, GL3.GL_INFO_LOG_LENGTH, infoLogLength);
            ByteBuffer logMessage = ByteBuffer.allocate(infoLogLength.get(0));
            gl.glGetShaderInfoLog(shaderId, infoLogLength.get(0), result, logMessage);
            String message = new String(logMessage.array());
            throw new RuntimeException("shader compiling error" + message);
        }
        return shaderId;
    }

    private static int unsignedByte(byte value) {
        return value & 0xFF;
    }

    public static BufferedImage createTGAImage(byte[] buff) throws IOException {
        int offset, width = 0, height = 0;
        int[] tgaBuffer = null;

        if (buff[2] == 0x02) { // BGRA File

            offset = 12;
            width = (unsignedByte(buff[offset + 1]) << 8 | unsignedByte(buff[offset]));

            offset = 14;
            height = (unsignedByte(buff[offset + 1]) << 8 | unsignedByte(buff[offset]));

            int colorDepth = unsignedByte(buff[offset + 2]);

            if (colorDepth == 0x20) { // 32 bits depth
                offset = 18;

                int count = width * height;
                tgaBuffer = new int[count];

                for (int i = 0; i < count; i++) {
                    byte b = buff[offset++]; //This is for didatic prupose, you can remove it and make inline covert.
                    byte g = buff[offset++];
                    byte r = buff[offset++];
                    byte a = buff[offset++];

                    tgaBuffer[i] = ((a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF);
                }
            }
        }

        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        result.setRGB(0, 0, width, height, tgaBuffer, 0, width);
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        newImage.getGraphics().drawImage(result, 0, 0, null);
        ImageIO.write(newImage, "png", new File("F:/1.png"));
        return newImage;
    }
}
