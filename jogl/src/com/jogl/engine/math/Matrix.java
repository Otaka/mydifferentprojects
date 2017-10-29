package com.jogl.engine.math;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLException;
import com.jogamp.opengl.fixedfunc.GLMatrixFunc;
import static com.jogamp.opengl.fixedfunc.GLMatrixFunc.*;
import com.jogamp.opengl.math.*;
import java.nio.FloatBuffer;
import jogamp.common.os.PlatformPropsImpl;

/**
 * @author Dmitry
 */
public class Matrix {

    /**
     * @param matrixModeName One of
     * {@link GLMatrixFunc#GL_MODELVIEW GL_MODELVIEW}, {@link GLMatrixFunc#GL_PROJECTION GL_PROJECTION}
     * or {@link GL#GL_TEXTURE GL_TEXTURE}
     * @return true if the given matrix-mode name is valid, otherwise false.
     */
    public static final boolean isMatrixModeName(final int matrixModeName) {
        return true;

    }

    /**
     * @param matrixModeName One of
     * {@link GLMatrixFunc#GL_MODELVIEW GL_MODELVIEW}, {@link GLMatrixFunc#GL_PROJECTION GL_PROJECTION}
     * or {@link GL#GL_TEXTURE GL_TEXTURE}
     * @return The corresponding matrix-get name, one of
     * {@link GLMatrixFunc#GL_MODELVIEW_MATRIX GL_MODELVIEW_MATRIX}, {@link GLMatrixFunc#GL_PROJECTION_MATRIX GL_PROJECTION_MATRIX}
     * or {@link GLMatrixFunc#GL_TEXTURE_MATRIX GL_TEXTURE_MATRIX}
     */
    public static final int matrixModeName2MatrixGetName(final int matrixModeName) {

        return GL_MODELVIEW_MATRIX;

    }

    /**
     * @param matrixGetName One of
     * {@link GLMatrixFunc#GL_MODELVIEW_MATRIX GL_MODELVIEW_MATRIX}, {@link GLMatrixFunc#GL_PROJECTION_MATRIX GL_PROJECTION_MATRIX}
     * or {@link GLMatrixFunc#GL_TEXTURE_MATRIX GL_TEXTURE_MATRIX}
     * @return true if the given matrix-get name is valid, otherwise false.
     */
    public static final boolean isMatrixGetName(final int matrixGetName) {

        return true;

    }

    /**
     * @param matrixGetName One of
     * {@link GLMatrixFunc#GL_MODELVIEW_MATRIX GL_MODELVIEW_MATRIX}, {@link GLMatrixFunc#GL_PROJECTION_MATRIX GL_PROJECTION_MATRIX}
     * or {@link GLMatrixFunc#GL_TEXTURE_MATRIX GL_TEXTURE_MATRIX}
     * @return The corresponding matrix-mode name, one of
     * {@link GLMatrixFunc#GL_MODELVIEW GL_MODELVIEW}, {@link GLMatrixFunc#GL_PROJECTION GL_PROJECTION}
     * or {@link GL#GL_TEXTURE GL_TEXTURE}
     */
    public static final int matrixGetName2MatrixModeName(final int matrixGetName) {
        return GL_MODELVIEW;

    }

    /**
     * @param sb optional passed StringBuilder instance to be used
     * @param f the format string of one floating point, i.e. "%10.5f", see
     * {@link java.util.Formatter}
     * @param a 4x4 matrix in column major order (OpenGL)
     * @return matrix string representation
     */
    @SuppressWarnings("deprecation")
    public static StringBuilder matrixToString(final StringBuilder sb, final String f, final FloatBuffer a) {
        return FloatUtil.matrixToString(sb, null, f, a, 0, 4, 4, false);
    }

    /**
     * @param sb optional passed StringBuilder instance to be used
     * @param f the format string of one floating point, i.e. "%10.5f", see
     * {@link java.util.Formatter}
     * @param a 4x4 matrix in column major order (OpenGL)
     * @param b 4x4 matrix in column major order (OpenGL)
     * @return side by side representation
     */
    @SuppressWarnings("deprecation")
    public static StringBuilder matrixToString(final StringBuilder sb, final String f, final FloatBuffer a, final FloatBuffer b) {
        return FloatUtil.matrixToString(sb, null, f, a, 0, b, 0, 4, 4, false);
    }

    /**
     * Creates an instance of PMVMatrix.
     * <p>
     * Implementation uses non-direct non-NIO Buffers with guaranteed backing
     * array, which allows faster access in Java computation.
     * </p>
     */
    public Matrix() {
        // I    Identity
        // T    Texture
        // P    Projection
        // Mv   ModelView
        // Mvi  Modelview-Inverse
        // Mvit Modelview-Inverse-Transpose
        matrixArray = new float[16];
        matrixMv = Buffers.slice2Float(matrixArray, 0, 16);

        mat4Tmp1 = new float[16];
        mat4Tmp2 = new float[16];
        mat4Tmp3 = new float[16];
        matrixTxSx = new float[16];
        FloatUtil.makeIdentity(matrixTxSx);
        reset();
    }

    /**
     * Issues {@link #loadIdentity()} on all matrices, i.e.
     * {@link GLMatrixFunc#GL_MODELVIEW GL_MODELVIEW}, {@link GLMatrixFunc#GL_PROJECTION GL_PROJECTION}
     * or {@link GL#GL_TEXTURE GL_TEXTURE} and resets all internal states.
     */
    public final void reset() {
        FloatUtil.makeIdentity(matrixArray, 0);
    }

    public final FloatBuffer getFloatBuffer() {
        return matrixMv;
    }

    public final void glGetFloatv(final FloatBuffer params) {
        final int pos = params.position();
        final FloatBuffer matrix = getFloatBuffer();
        params.put(matrix); // matrix -> params
        matrix.reset();
        params.position(pos);
    }

    public final void glGetFloatv(final float[] params, final int params_offset) {
        final FloatBuffer matrix = getFloatBuffer();
        matrix.get(params, params_offset, 16); // matrix -> params
        matrix.reset();
    }

    public final void loadMatrix(final float[] values, final int offset) {
        matrixMv.put(values, offset, 16);
        matrixMv.reset();
    }

    public final void loadMatrix(final java.nio.FloatBuffer m) {
        final int spos = m.position();
        matrixMv.put(m);
        matrixMv.reset();
        m.position(spos);
    }

    public final void loadFromMatrix(Matrix matrix) {
        FloatBuffer m = matrix.getFloatBuffer();
        final int spos = m.position();
        matrixMv.put(m);
        matrixMv.reset();
        m.position(spos);
    }

    /**
     * Load the current matrix with the values of the given {@link Quaternion}'s
     * rotation {@link Quaternion#toMatrix(float[], int) matrix representation}.
     */
    public final void loadMatrix(final Quaternion quat) {
        quat.toMatrix(matrixArray, 0);
        matrixMv.reset();
    }

    public final void loadIdentity() {
        FloatUtil.makeIdentity(matrixArray, 0);
    }

    public final void multMatrix(final FloatBuffer m) {
        FloatUtil.multMatrix(matrixMv, m);
    }

    public void multVectorOnMatrix(Vector4 source, Vector4 dest) {
        FloatUtil.multMatrixVec(matrixMv, source.getBuffer(), dest.getBuffer());
    }

    public final void multMatrix(Matrix matrix) {
        FloatUtil.multMatrix(matrixMv, matrix.getFloatBuffer());
    }

    public final void multMatrix(final float[] m, final int m_offset) {
        FloatUtil.multMatrix(matrixArray, 0, m, m_offset);
    }

    public final void translate(final float x, final float y, final float z) {
        multMatrix(FloatUtil.makeTranslation(matrixTxSx, false, x, y, z), 0);
    }

    public final void scale(final float x, final float y, final float z) {
        multMatrix(FloatUtil.makeScale(matrixTxSx, false, x, y, z), 0);
    }

    /**
     * Rotate by axis angle
     */
    public final void rotate(final float ang_rad, final float x, final float y, final float z) {
        multMatrix(FloatUtil.makeRotationAxis(mat4Tmp1, 0, ang_rad, x, y, z, mat4Tmp2), 0);
    }

    /**
     * Rotate the current matrix with the given {@link Quaternion}'s rotation
     * {@link Quaternion#toMatrix(float[], int) matrix representation}.
     */
    public final void rotate(final Quaternion quat) {
        multMatrix(quat.toMatrix(mat4Tmp1, 0), 0);
    }

    public final void makeOrthoProjectionMatrix(final float left, final float right, final float bottom, final float top, final float zNear, final float zFar) {
        multMatrix(FloatUtil.makeOrtho(mat4Tmp1, 0, true, left, right, bottom, top, zNear, zFar), 0);
    }

    //
    // Extra functionality
    //
    /**
     * {@link #multMatrix(FloatBuffer) Multiply} the
     * {@link #glGetMatrixMode() current matrix} with the perspective/frustum
     * matrix.
     *
     * @param fovy_deg fov angle in degrees
     * @param aspect aspect ratio width / height
     * @param zNear
     * @param zFar
     * @throws GLException with GL_INVALID_VALUE if zNear is <= 0, or zFar < 0,
     * or if zNear == zFar.
     */
    public final void makePerspectiveProjectionMatrix(final float fovy_deg, final float aspect, final float zNear, final float zFar) throws GLException {
        multMatrix(FloatUtil.makePerspective(mat4Tmp1, 0, true, fovy_deg * FloatUtil.PI / 180.0f, aspect, zNear, zFar), 0);
    }

    /**
     * {@link #multMatrix(FloatBuffer) Multiply} and
     * {@link #translate(float, float, float) translate} the
     * {@link #glGetMatrixMode() current matrix} with the eye, object and
     * orientation.
     */
    public final void lookAt(final float eyex, final float eyey, final float eyez,
            final float centerx, final float centery, final float centerz,
            final float upx, final float upy, final float upz) {
        mat4Tmp2[0 + 0] = eyex;
        mat4Tmp2[1 + 0] = eyey;
        mat4Tmp2[2 + 0] = eyez;
        mat4Tmp2[0 + 4] = centerx;
        mat4Tmp2[1 + 4] = centery;
        mat4Tmp2[2 + 4] = centerz;
        mat4Tmp2[0 + 8] = upx;
        mat4Tmp2[1 + 8] = upy;
        mat4Tmp2[2 + 8] = upz;
        multMatrix(FloatUtil.makeLookAt(mat4Tmp1, 0, mat4Tmp2 /* eye */, 0, mat4Tmp2 /* center */, 4, mat4Tmp2 /* up */, 8, mat4Tmp3), 0);
    }

    public void rotate(float angle, float x, float y, float z, boolean normalise) {
        // Normalize the axis vector
        if (normalise) {
            float length = x * x + y * y + z * z;
            if ((length != 1.0f) && (length != 0.0f)) {
                // Invert length to reduce number of later divisions.
                length = 1.0f / (float) Math.sqrt(length);

                x *= length;
                y *= length;
                z *= length;
            }
        }

        angle = (float) Math.toRadians(angle);

        /* Slightly optimised matrix calculation for generic rotation. Note that
         * SSE implementations might end up being faster, if implemented.
         */
        float[] result = new float[16];

        float cosa = (float) Math.cos(angle);
        float sina = (float) Math.sin(angle);
        float mcosa = 1.0f - cosa;
        float m0 = (x * x * mcosa) + cosa;
        float m1 = (x * y * mcosa) + (z * sina);
        float m2 = (x * z * mcosa) - (y * sina);

        float m4 = (x * y * mcosa) - (z * sina);
        float m5 = (y * y * mcosa) + cosa;
        float m6 = (y * z * mcosa) + (x * sina);

        float m8 = (x * z * mcosa) + (y * sina);
        float m9 = (y * z * mcosa) - (x * sina);
        float m10 = (z * z * mcosa) + cosa;

        for (int i = 0; i < 4; i++) {
            result[0 + i] = (matrixArray[i] * m0) + (matrixArray[i + 4] * m1) + (matrixArray[i + 8] * m2);
            result[4 + i] = (matrixArray[i] * m4) + (matrixArray[i + 4] * m5) + (matrixArray[i + 8] * m6);
            result[8 + i] = (matrixArray[i] * m8) + (matrixArray[i + 4] * m9) + (matrixArray[i + 8] * m10);
            result[12 + i] = (matrixArray[i + 12]);
        }

        System.arraycopy(result, 0, matrixArray, 0, 16);
    }

    public StringBuilder toString(StringBuilder sb, final String f) {
        if (null == sb) {
            sb = new StringBuilder();
        }

        sb.append("Modelview").append(PlatformPropsImpl.NEWLINE);
        matrixToString(sb, f, matrixMv);
        return sb;
    }

    @Override
    public String toString() {
        return toString(null, "%10.5f").toString();
    }

    private float[] matrixArray;
    private FloatBuffer matrixMv;
    private float[] matrixTxSx;
    private float[] mat4Tmp1, mat4Tmp2, mat4Tmp3;
}
