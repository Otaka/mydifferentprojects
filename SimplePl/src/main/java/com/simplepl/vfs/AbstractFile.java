package com.simplepl.vfs;

import com.simplepl.Const;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import org.apache.commons.io.IOUtils;

/**
 * @author sad
 */
public abstract class AbstractFile implements NamedModuleObject {

    /**
    Get name of the file without extension
     */
    @Override
    public abstract String getName();

    /**
    Get name of the file as it is, with extension
     */
    public abstract String getRawName();

    public abstract OutputStream getOutputStream();

    public abstract InputStream getInputStream();

    public boolean isSource() {
        return getRawName().toLowerCase().endsWith(Const.EXT);
    }

    public String readToString() {
        try {
            try (InputStream stream = getInputStream()) {
                return IOUtils.toString(stream, "UTF-8");
            }
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }
}
