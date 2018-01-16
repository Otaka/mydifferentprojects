package com.sqlprocessor.sqlplan.items;

import com.sqlprocessor.compiler.ExpressionExecutor;
import com.sqlprocessor.compiler.SourceCode;
import com.sqlprocessor.sqlplan.AbstractPlanItem;
import com.sqlprocessor.table.SqlTable;
import com.sqlprocessor.table.TableManager;
import com.sqlprocessor.utils.StringBuilderWithPadding;
import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.expression.Expression;

/**
 * @author sad
 */
public class FilterPlanItem extends AbstractPlanItem {

    private Expression expression;
    private List<SqlTable> usedTables = new ArrayList<SqlTable>();
    private TableManager tableManager;

    public FilterPlanItem(Expression expression, TableManager tableManager) {
        this.expression = expression;
        this.tableManager = tableManager;
    }

    public void setUsedTables(List<SqlTable> usedTables) {
        this.usedTables = usedTables;
    }

    public Expression getExpression() {
        return expression;
    }

    public List<SqlTable> getUsedTables() {
        return usedTables;
    }

    @Override
    public void generateSourceCode(TableManager tableManager, StringBuilderWithPadding sourceCode, SourceCode sourceCodeGenerator) {
        ExpressionExecutor.ExpressionExecutorResult result = new ExpressionExecutor().executeExpression(expression, tableManager, sourceCodeGenerator,"combinedWorkingTable");
        if (!(result.resultType == Boolean.class || result.resultType == boolean.class)) {
            throw new IllegalArgumentException("Expression in WHERE statement should return boolean, but it returns " + result.resultType.getSimpleName() + ". Expression [" + expression + "]");
        }

        sourceCode.print("if(!(").print(result.expressionExecSourceCode).println(")){");
        sourceCode.incLevel();
        sourceCode.println("continue;");
        sourceCode.decLevel();
        sourceCode.println("}");
    }
}
