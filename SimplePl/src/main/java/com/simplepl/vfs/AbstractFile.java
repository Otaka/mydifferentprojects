package com.simplepl.vfs;

import com.simplepl.Const;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author sad
 */
public abstract class AbstractFile implements NamedPackageObject {

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
}
