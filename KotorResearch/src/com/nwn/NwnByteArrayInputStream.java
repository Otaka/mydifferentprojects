package nwn;

import java.io.ByteArrayInputStream;

/**
 * @author sad
 */
public class NwnByteArrayInputStream extends ByteArrayInputStream {

    public NwnByteArrayInputStream(byte[] buf) {
        super(buf);
    }

    public void setPosition(int pos) {
        this.pos = pos;
    }

    public int getPosition() {
        return pos;
    }

}
