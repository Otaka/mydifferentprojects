package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class CreateTableAst extends Ast {

    private boolean localTemp = false;
    private TableNameAst tableName;
    private boolean onCommitPreserveRows = false;
    private boolean includingProjections = false;

    private SelectAst asSelect;
    private TableNameAst likeTableName;

    public TableNameAst getLikeTableName() {
        return likeTableName;
    }

    public void setIncludingProjections(boolean includingProjections) {
        this.includingProjections = includingProjections;
    }

    public boolean isIncludingProjections() {
        return includingProjections;
    }

    public void setLikeTableName(TableNameAst likeTableName) {
        this.likeTableName = likeTableName;
    }

    public void setAsSelect(SelectAst asSelect) {
        this.asSelect = asSelect;
    }

    public SelectAst getAsSelect() {
        return asSelect;
    }

    public void setOnCommitPreserveRows(boolean onCommitPreserveRows) {
        this.onCommitPreserveRows = onCommitPreserveRows;
    }

    public boolean isOnCommitPreserveRows() {
        return onCommitPreserveRows;
    }

    public void setLocalTemp(boolean localTemp) {
        this.localTemp = localTemp;
    }

    public void setTableName(TableNameAst tableName) {
        this.tableName = tableName;
    }

    public TableNameAst getTableName() {
        return tableName;
    }

    public boolean isLocalTemp() {
        return localTemp;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print("CREATE");
        if (localTemp) {
            context.print(" LOCAL TEMP");
        }
        context.print(" TABLE ");
        context.print(tableName);
        if (onCommitPreserveRows) {
            context.print(" ON COMMIT PRESERVE ROWS");
        }

        if (asSelect != null) {
            context.print(" AS ").print(asSelect);
        } else if (likeTableName != null) {
            context.print(" LIKE ").print(likeTableName);
        }else{
            throw new RuntimeException("create table mode is not implemented. Only 'create as select' or 'create like table' allowed now");
        }
        if(includingProjections){
            context.print(" INCLUDING PROJECTIONS");
        }
    }
}
