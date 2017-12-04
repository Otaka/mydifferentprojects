package com.sqlprocessor.compiler;

import com.sqlprocessor.buffers.SqlBuffer;

/**
 * @author sad
 */
public abstract class SqlExecutor {
    public abstract void process();
    public abstract void setSqlBuffer(SqlBuffer sqlBuffer);
    public abstract SqlBuffer createOutputSqlBuffer(String bufferName);
}
