package com.sqlprocessor.compiler;

import com.sqlprocessor.buffers.SqlBuffer;

/**
 * @author sad
 */
public abstract class SqlExecutor {
    public abstract void process();
    public abstract void setSqlBuffer(SqlBuffer sqlBuffer);
    public abstract SqlBuffer createOutputSqlBuffer(String bufferName);
    public boolean objectEquals(Object obj1, Object obj2){
        if(obj1==null&&obj2==null){
            return true;
        }
        if(obj1==null||obj2==null){
            return false;
        }
        return obj1.equals(obj2);
    }
}
