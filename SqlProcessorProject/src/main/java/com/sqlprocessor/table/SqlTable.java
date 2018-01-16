package com.sqlprocessor.table;

import com.sqlprocessor.join.JoinType;
import com.sqlprocessor.buffers.SqlBuffer;

/**
 * @author sad
 *
 * SqlTable is a wrapper above the SqlBuffer(storage for data), because you can do something like this:
 * select * from table a, table b
 * same table, but it referenced two times. That is why this "table" - SqlBuffer, and "table a","table b" - it is SqlTable
 */
public class SqlTable {

    private int id;
    private String name;
    private String alias;
    private SqlBuffer buffer;
    private JoinType joinType;

    public SqlTable(int id, String name, String alias, SqlBuffer buffer, JoinType joinType) {
        this.id = id;
        this.name = name;
        this.alias = alias;
        this.buffer = buffer;
        this.joinType = joinType;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public String getAlias() {
        return alias;
    }

    public SqlBuffer getBuffer() {
        return buffer;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        if (alias != null) {
            return name + " " + alias;
        }

        return name;
    }
}
