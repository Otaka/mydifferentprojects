package com.sqlprocessor.compiler;

import com.sqlprocessor.sqlplan.items.FilterPlanItem;
import com.sqlprocessor.table.SqlTable;
import com.sqlprocessor.table.TableManager;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnalyticExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.CastExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExtractExpression;
import net.sf.jsqlparser.expression.HexValue;
import net.sf.jsqlparser.expression.IntervalExpression;
import net.sf.jsqlparser.expression.JdbcNamedParameter;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.JsonExpression;
import net.sf.jsqlparser.expression.MySQLGroupConcat;
import net.sf.jsqlparser.expression.NumericBind;
import net.sf.jsqlparser.expression.OracleHierarchicalExpression;
import net.sf.jsqlparser.expression.OracleHint;
import net.sf.jsqlparser.expression.SignedExpression;
import net.sf.jsqlparser.expression.TimeKeyExpression;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.UserVariable;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.WithinGroupExpression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MultiExpressionList;
import net.sf.jsqlparser.expression.operators.relational.RegExpMatchOperator;
import net.sf.jsqlparser.expression.operators.relational.RegExpMySQLOperator;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;

/**
 * @author sad
 */
public class ConditionProcessor {

    private TableManager tableManager;

    public ConditionProcessor(TableManager tableManager) {
        this.tableManager = tableManager;
    }

    /**
    Currently as extraction(index/where expression) supported only "column=column" "column=value" expressions.<br>
    if we have complex expression like "column1=column2 and column1=3 and (column4=34 or column4=35)" then "column1=column2" and "column1=3" can be extracted
     */
    public List<FilterPlanItem> splitWhereExpression(Expression expression) {
        List<FilterPlanItem> filterPlanItem = new ArrayList<>();
        if (expression instanceof EqualsTo) {
            FilterPlanItem planItem = createFilterPlanItemFrom((EqualsTo) expression);
            filterPlanItem.add(planItem);
        } else if (expression instanceof AndExpression) {
            AndExpression andExpression = (AndExpression) expression;
            List<FilterPlanItem> leftFilters = splitWhereExpression(andExpression.getLeftExpression());
            List<FilterPlanItem> rightFilters = splitWhereExpression(andExpression.getRightExpression());
            filterPlanItem.addAll(leftFilters);
            filterPlanItem.addAll(rightFilters);
        } else {
            filterPlanItem.add(createFilterPlanItemFrom(expression));
        }

        return filterPlanItem;
    }

    private FilterPlanItem createFilterPlanItemFrom(Expression expression) {
        FilterPlanItem planItem = new FilterPlanItem(expression, tableManager);
        planItem.setUsedTables(collectTablesFromExpression(expression));
        return planItem;
    }

    private List<SqlTable> collectTablesFromExpression(Expression expression) {
        Set<Integer> usedTablesIds = collectTablesIdsFromExpression(expression);
        List<SqlTable> usedTables = new ArrayList<SqlTable>();
        for (Integer tableId : usedTablesIds) {
            usedTables.add(tableManager.getTableById(tableId));
        }
        return usedTables;
    }

    public Set<Integer> collectTablesIdsFromExpression(Expression expression) {
        final Set<Integer> foundTablesIds = new HashSet<>();
        expression.accept(new ExpressionDeParser() {
            @Override
            public void visit(Column tableColumn) {
                super.visit(tableColumn);
                if (tableColumn.toString().equalsIgnoreCase("true")||tableColumn.toString().equalsIgnoreCase("false")) {
                    return;
                }

                SqlTable table = tableManager.searchTableByColumn(tableColumn);
                if (table == null) {
                    throw new IllegalArgumentException("Cannot find table for column ["+tableColumn+"]");
                }

                foundTablesIds.add(table.getId());
            }

            @Override
            public void visit(AnalyticExpression arg) {
                throwNotSupportedException(arg);
            }

            @Override
            public void visit(AnyComparisonExpression arg) {
                throwNotSupportedException(arg);
            }

            @Override
            public void visit(AllComparisonExpression allComparisonExpression) {
                throwNotSupportedException(allComparisonExpression);
            }

            @Override
            public void visit(Between arg) {
                throwNotSupportedException(arg);
            }

            @Override
            public void visit(CaseExpression caseExpression) {
                throwNotSupportedException(caseExpression);
            }

            @Override
            public void visit(CastExpression cast) {
                throwNotSupportedException(cast);
            }

            @Override
            public void visit(ExistsExpression existsExpression) {
                throwNotSupportedException(existsExpression);
            }

            @Override
            public void visit(ExtractExpression eexpr) {
                throwNotSupportedException(eexpr);
            }

            @Override
            public void visit(HexValue hexValue) {
                throwNotSupportedException(hexValue);
            }

            @Override
            public void visit(IntervalExpression iexpr) {
                throwNotSupportedException(iexpr);
            }

            @Override
            public void visit(InExpression inExpression) {
                throwNotSupportedException(inExpression);
            }

            @Override
            public void visit(JdbcNamedParameter jdbcNamedParameter) {
                throwNotSupportedException(jdbcNamedParameter);
            }

            @Override
            public void visit(JsonExpression jsonExpr) {
                throwNotSupportedException(jsonExpr);
            }

            @Override
            public void visit(JdbcParameter jdbcParameter) {
                throwNotSupportedException(jdbcParameter);
            }

            @Override
            public void visit(Matches matches) {
                throwNotSupportedException(matches);
            }

            @Override
            public void visit(MultiExpressionList multiExprList) {
                throwNotSupportedException(multiExprList);
            }

            @Override
            public void visit(MySQLGroupConcat groupConcat) {
                throwNotSupportedException(groupConcat);
            }

            @Override
            public void visit(NumericBind bind) {
                throwNotSupportedException(bind);
            }

            @Override
            public void visit(OracleHierarchicalExpression oexpr) {
                throwNotSupportedException(oexpr);
            }

            @Override
            public void visit(OracleHint hint) {
                throwNotSupportedException(hint);
            }

            @Override
            public void visit(RegExpMatchOperator rexpr) {
                throwNotSupportedException(rexpr);
            }

            @Override
            public void visit(RegExpMySQLOperator rexpr) {
                throwNotSupportedException(rexpr);
            }

            @Override
            public void visit(SubSelect subSelect) {
                throwNotSupportedException(subSelect);
            }

            @Override
            public void visit(TimeKeyExpression timeKeyExpression) {
                throwNotSupportedException(timeKeyExpression);
            }

            @Override
            public void visit(TimeValue timeValue) {
                throwNotSupportedException(timeValue);
            }

            @Override
            public void visit(SignedExpression signedExpression) {
                throwNotSupportedException(signedExpression);
            }

            @Override
            public void visit(TimestampValue timestampValue) {
                throwNotSupportedException(timestampValue);
            }

            @Override
            public void visit(UserVariable var) {
                throwNotSupportedException(var);
            }

            @Override
            public void visit(WhenClause whenClause) {
                throwNotSupportedException(whenClause);
            }

            @Override
            public void visit(WithinGroupExpression wgexpr) {
                throwNotSupportedException(wgexpr);
            }
        });
        return foundTablesIds;
    }

    private void throwNotSupportedException(Object ast) {
        throw new IllegalArgumentException(ast.getClass().getSimpleName() + " are not supported yet");
    }
}
