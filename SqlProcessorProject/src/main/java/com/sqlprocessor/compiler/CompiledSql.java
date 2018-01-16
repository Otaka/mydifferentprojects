package com.sqlprocessor.compiler;

import com.sqlprocessor.buffers.SqlBuffer;

/**
 * @author sad
 */
public class CompiledSql {

    private Class sqlExecutorClass;
    private SqlBuffer[] buffers;

    public CompiledSql(Class sqlExecutorClass, SqlBuffer[] buffers) {
        this.sqlExecutorClass = sqlExecutorClass;
        this.buffers = buffers;
    }

    public Class getSqlExecutorClass() {
        return sqlExecutorClass;
    }

    public SqlExecutor getNewSqlExecutor() throws Exception {
        SqlExecutor executor = (SqlExecutor) sqlExecutorClass.newInstance();
        for (SqlBuffer buffer : buffers) {
            executor.setSqlBuffer(buffer);
        }

        return executor;
    }

}
