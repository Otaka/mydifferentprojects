package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;

/**
 * @author sad
 */
public class JoinAst extends Ast {

    private String joinType;
    private Ast table;
    private Ast checkExpression;

    public JoinAst(String joinType) {
        this.joinType = fixJoinType(joinType);
    }
    
    private String fixJoinType(String joinType){
        return joinType.replace('\n', ' ').replace('\r', ' ').replace('\t', ' ').replaceAll("\\s\\s+", " ").toUpperCase();
    }

    public void setTable(Ast table) {
        this.table = table;
    }

    public Ast getTable() {
        return table;
    }

    public String getJoinType() {
        return joinType;
    }

    public void setCheckExpression(Ast checkExpression) {
        this.checkExpression = checkExpression;
    }

    public Ast getCheckExpression() {
        return checkExpression;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print(joinType).print(" ").print(table).print(" ON ").print(checkExpression);
    }

}
