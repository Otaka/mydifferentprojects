package com.sqlprocessor.table;

import com.sqlprocessor.buffers.SqlBuffer;
import com.sqlprocessor.join.JoinType;
import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * @author sad
 */
public class TableManager {

    private int nextFreeTableIndex = -1;
    private SqlBuffer[] buffers;
    private List<SqlTable> sqlTables = new ArrayList<SqlTable>();
    private String combinedWorkingTableRowClassName;
    private String outputRowClassName;

    public void setOutputRowClassName(String outputRowClassName) {
        this.outputRowClassName = outputRowClassName;
    }

    public String getOutputRowClassName() {
        return outputRowClassName;
    }

    public void setCombinedWorkingTableRowClassName(String combinedWorkingTableRowClassName) {
        this.combinedWorkingTableRowClassName = combinedWorkingTableRowClassName;
    }

    public String getCombinedWorkingTableRowClassName() {
        return combinedWorkingTableRowClassName;
    }

    public int getNextFreeTableIndex() {
        nextFreeTableIndex++;
        return nextFreeTableIndex;
    }

    public void registerBuffers(SqlBuffer[] buffers) {
        this.buffers = buffers;
    }

    public SqlBuffer[] getBuffers() {
        return buffers;
    }

    public SqlField getSqlFieldByColumn(Column column) {
        SqlTable table = searchTableByColumn(column);
        if (table == null) {
            throw new IllegalArgumentException("Cannot find table for column [" + column + "]");
        }

        return new SqlField(table, column.getColumnName());
    }

    public SqlTable searchTableByColumn(Column column) {
        String columnName = column.getColumnName();
        if (column.getTable() == null||column.getTable().getName()==null) {
            SqlTable sqlTable = null;
            for (SqlTable table : sqlTables) {
                if (checkListContainsString(table.getBuffer().getFields(), columnName)) {
                    if (sqlTable != null) {
                        throw new IllegalArgumentException("Ambiguous field [" + columnName + "] it is in the table " + table.getBuffer().getName() + " and in the " + sqlTable.getBuffer().getName());
                    }
                    sqlTable = table;
                }
            }

            return sqlTable;
        } else {
            String tableName = column.getTable().getName();
            SqlTable table = searchTableByAlias(tableName);
            return table;
        }
    }

    private boolean checkListContainsString(List<String> list, String value) {
        for (String listValue : list) {
            if (listValue.contains(value)) {
                return true;
            }
        }

        return false;
    }

    public void collectTableNames(PlainSelect plainSelect) {
        FromItem fromItem = plainSelect.getFromItem();
        Table table = extractTable(fromItem);
        saveTable(table, JoinType.NONE);
        if (plainSelect.getJoins() != null) {
            for (Join join : plainSelect.getJoins()) {
                FromItem item = join.getRightItem();
                Table joinedTable = extractTable(item);
                JoinType joinType = getJoinType(join, joinedTable.getName());//can throw exception on wrong join type
                saveTable(joinedTable, joinType);
            }
        }
    }

    private JoinType getJoinType(Join join, String name) {
        if (join.isSimple()) {
            return JoinType.INNER;
        }

        if (join.isLeft() && join.isOuter()) {
            return JoinType.LEFT_OUTER;
        }

        throw new IllegalArgumentException("Unimplemented join on table [" + name + "] ");
    }

    public SqlTable getTableByIndex(int tableIndex) {
        if (tableIndex >= sqlTables.size()) {
            throw new IllegalArgumentException("Cannot get table [" + tableIndex + "] because you have only " + sqlTables.size() + " tables");
        }

        return sqlTables.get(tableIndex);
    }

    public SqlTable getTableById(int tableId) {
        for (SqlTable table : sqlTables) {
            if (table.getId() == tableId) {
                return table;
            }
        }

        return null;
    }

    public List<SqlTable> getSqlTables() {
        return sqlTables;
    }

    public String getTableAlias(Table tableExpression) {
        String name = tableExpression.getName();
        String alias = name;
        if (tableExpression.getAlias() != null) {
            alias = tableExpression.getAlias().getName();
        }
        return alias;
    }

    private void saveTable(Table tableExpression, JoinType joinType) {
        String alias = getTableAlias(tableExpression);
        String name = tableExpression.getName();

        int tableId = getNextFreeTableIndex();
        SqlBuffer buffer = searchBufferByName(name);
        if (buffer == null) {
            throw new IllegalArgumentException("Cannot find table [" + name + "]");
        }

        if (searchTableByAlias(alias) != null) {
            throw new IllegalArgumentException("Duplicated table/alias name [" + alias + "]. Please give table [" + name + "] another alias");
        }

        SqlTable table = new SqlTable(tableId, name, alias, buffer, joinType);
        sqlTables.add(table);
    }

    public SqlTable searchTableByAlias(String alias) {
        for (SqlTable sqlTable : sqlTables) {
            if (sqlTable.getAlias().equalsIgnoreCase(alias)) {
                return sqlTable;
            }
        }

        return null;
    }

    private SqlBuffer searchBufferByName(String name) {
        for (SqlBuffer buffer : getBuffers()) {
            if (buffer.getName().equalsIgnoreCase(name)) {
                return buffer;
            }
        }

        return null;
    }

    public static Table extractTable(FromItem fromItem) {
        if (!(fromItem instanceof Table)) {
            throw new UnsupportedOperationException("Select is supported only from table, but found " + fromItem.getClass().getSimpleName());
        }

        return (Table) fromItem;
    }
}
