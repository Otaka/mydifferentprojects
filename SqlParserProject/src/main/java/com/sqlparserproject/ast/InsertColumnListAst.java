package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class InsertColumnListAst extends Ast {

    private List<String> columnNames = new ArrayList<>();

    public void add(String columnName) {
        columnNames.add(columnName);
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.printList(columnNames, ",");
    }

}
