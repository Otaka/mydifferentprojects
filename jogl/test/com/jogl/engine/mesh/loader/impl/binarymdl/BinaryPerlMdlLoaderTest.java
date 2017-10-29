package com.jogl.engine.mesh.loader.impl.binarymdl;

import com.jogamp.opengl.math.Quaternion;
import org.junit.*;

/**
 *
 * @author Dmitry
 */
public class BinaryPerlMdlLoaderTest {

    public BinaryPerlMdlLoaderTest() {
    }

    @Test
    public void testPackedQuaternion() throws Exception {
        Quaternion q = BinaryPerlMdlLoader.convertPackedQuaternion(214538547145l);
        Assert.assertEquals(-0.00956754544780686f, q.getX(), 0.000001);
        Assert.assertEquals(-0.00960699924346792f, q.getY(), 0.000001);
        Assert.assertEquals(-0.99990807959514f, q.getZ(), 0.000001);
        Assert.assertEquals(0, q.getW(), 0.00001);

        q = BinaryPerlMdlLoader.convertPackedQuaternion(2145385471l);
        Assert.assertEquals(0.0f, q.getX(), 0.000001);
        Assert.assertEquals(0.0f, q.getY(), 0.000001);
        Assert.assertEquals(0.0f, q.getZ(), 0.000001);
        Assert.assertEquals(-1, q.getW(), 0.000001);
    }

}
