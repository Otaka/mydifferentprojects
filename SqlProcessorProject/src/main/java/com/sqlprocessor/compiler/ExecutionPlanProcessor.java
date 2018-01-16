package com.sqlprocessor.compiler;

import com.sqlprocessor.join.JoinType;
import com.sqlprocessor.join.LeftJoinTableColumnColumn;
import com.sqlprocessor.sqlplan.AbstractPlanItem;
import com.sqlprocessor.sqlplan.ExecutionPlan;
import com.sqlprocessor.sqlplan.items.FilterPlanItem;
import com.sqlprocessor.sqlplan.items.GroupPlanItem;
import com.sqlprocessor.sqlplan.items.IterateOverTableLeftJoinPlanItem;
import com.sqlprocessor.sqlplan.items.IterateOverTablePlanItem;
import com.sqlprocessor.sqlplan.items.SortPlanItem;
import com.sqlprocessor.table.SqlField;
import com.sqlprocessor.table.SqlSortField;
import com.sqlprocessor.table.SqlTable;
import com.sqlprocessor.table.TableManager;
import java.util.ArrayList;
import java.util.List;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;

/**
 * @author sad
 */
public class ExecutionPlanProcessor {

    private TableManager tableManager;

    public ExecutionPlanProcessor(TableManager tableManager) {
        this.tableManager = tableManager;
    }

    public ExecutionPlan createExecutionPlan(PlainSelect plainSelect) {
        ExecutionPlan plan = new ExecutionPlan();
        List<SqlTable> tables = new ArrayList<SqlTable>();
        tables.addAll(tableManager.getSqlTables());//create a copy from the table list

        List<AbstractPlanItem> loopPlanItems = new ArrayList<>();
        SqlTable mainTable = tables.remove(0);
        IterateOverTablePlanItem iterateOverMainTable = new IterateOverTablePlanItem(mainTable);
        loopPlanItems.add(iterateOverMainTable);
        plan.setStage1LoopPlanItems(loopPlanItems);
        List<SqlTable> simpleJoinTables = extractAndRemoveSimpleJoinTables(tables);

        IterateOverTablePlanItem currentIterator = iterateOverMainTable;
        for (SqlTable sqlTable : simpleJoinTables) {
            IterateOverTablePlanItem tableIterator = new IterateOverTablePlanItem(sqlTable);
            currentIterator.addChildItems(tableIterator);
            currentIterator = tableIterator;
        }

        List<LeftJoinTableColumnColumn> leftJoinTables = extractJoins(plainSelect);
        for (LeftJoinTableColumnColumn leftJoinTable : leftJoinTables) {
            IterateOverTableLeftJoinPlanItem leftJoinPlanItem = new IterateOverTableLeftJoinPlanItem(leftJoinTable);
            currentIterator.addChildItems(leftJoinPlanItem);
        }

        Expression whereExpression = plainSelect.getWhere();
        if (whereExpression != null) {
            ConditionProcessor conditionProcessor = new ConditionProcessor(tableManager);
            List<FilterPlanItem> filterItems = conditionProcessor.splitWhereExpression(whereExpression);
            currentIterator.addChildItems(filterItems);
        }

        if (plainSelect.getGroupByColumnReferences() != null) {
            List<SqlField> groupingFields = extractFieldsFromExpressionsList(plainSelect.getGroupByColumnReferences(), "'Group By' ");
            //To make grouping we first sort elements by grouping fields, and after that we will be able to process grouped columns in one run
            List<AbstractPlanItem> groupByPlanItems = new ArrayList<>();
            SortPlanItem sortPlanItem = createSortPlanItemForGroupBy(groupingFields);
            groupByPlanItems.add(sortPlanItem);//

            GroupPlanItem groupPlanItem = createGroupPlanItem(groupingFields);
            groupByPlanItems.add(groupPlanItem);//
            plan.setGroupingFields(groupingFields);
            plan.setStage2GroupByPlanItems(groupByPlanItems);
        }

        if (plainSelect.getHaving() != null) {
            throw new IllegalArgumentException("'Having' is not supported now");
        }

        if (plainSelect.getOrderByElements() != null) {
            SortPlanItem sortPlanItem = createSortPlanItem(plainSelect.getOrderByElements());
            List<AbstractPlanItem> sortPlanItems = new ArrayList<>();
            sortPlanItems.add(sortPlanItem);
            plan.setStage3SortPlanItems(sortPlanItems);
        }

        return plan;
    }

    private SortPlanItem createSortPlanItem(List<OrderByElement> orderByFields) {
        List<SqlSortField> sortFields = new ArrayList<>();
        for (OrderByElement orderByField : orderByFields) {
            Expression expression = orderByField.getExpression();
            if (expression instanceof Column) {
                Column column = (Column) expression;
                SqlField sqlField = tableManager.getSqlFieldByColumn(column);
                SqlSortField.SortOrder sortOrder = orderByField.isAsc() ? SqlSortField.SortOrder.ASC : SqlSortField.SortOrder.DESC;
                sortFields.add(new SqlSortField(sqlField, sortOrder));
            } else {
                throw new IllegalArgumentException("'ORDER BY' should have only 'COLUMN ORDER_DIRECTION' or 'TABLE.COLUMN ORDER_DIRECTION', but found [" + expression + "]");
            }
        }

        SortPlanItem sortPlanItem = new SortPlanItem(sortFields);
        return sortPlanItem;
    }

    private SortPlanItem createSortPlanItemForGroupBy(List<SqlField> sqlFields) {
        List<SqlSortField> sortFields = new ArrayList<>();
        for (SqlField sqlField : sqlFields) {
            sortFields.add(new SqlSortField(sqlField));
        }

        SortPlanItem sortPlanItem = new SortPlanItem(sortFields);
        return sortPlanItem;
    }

    private GroupPlanItem createGroupPlanItem(List<SqlField> sqlFields) {
        return new GroupPlanItem(sqlFields);
    }

    private List<SqlField> extractFieldsFromExpressionsList(List<Expression> groupExpressions, String componentName) {
        List<SqlField> sqlFields = new ArrayList<>();
        for (Expression expression : groupExpressions) {
            if (expression instanceof Column) {
                Column column = (Column) expression;
                SqlField sqlField = tableManager.getSqlFieldByColumn(column);
                sqlFields.add(sqlField);
            } else {
                throw new IllegalArgumentException(componentName + " should have only COLUMN or TABLE.COLUMN, but found " + expression);
            }
        }

        return sqlFields;
    }

    private List<LeftJoinTableColumnColumn> extractJoins(PlainSelect selectStatement) {
        List<LeftJoinTableColumnColumn> tables = new ArrayList<>();
        if (selectStatement.getJoins() != null) {
            for (Join join : selectStatement.getJoins()) {
                if (join.isLeft()) {
                    String alias = tableManager.getTableAlias(TableManager.extractTable(join.getRightItem()));
                    SqlTable sqlTable = tableManager.searchTableByAlias(alias);
                    if (sqlTable == null) {
                        throw new IllegalStateException("Error, cannot find sqlTable by alias [" + alias + "]");
                    }

                    LeftJoinTableColumnColumn leftJoinTable = createLeftJoinTable(sqlTable, join);
                    tables.add(leftJoinTable);
                }
            }
        }
        return tables;
    }

    private List<SqlTable> extractAndRemoveSimpleJoinTables(List<SqlTable> tables) {
        List<SqlTable> sqlSimpleJoinTables = new ArrayList<>();
        for (int i = 0; i < tables.size(); i++) {
            SqlTable sqlTable = tables.get(i);
            if (sqlTable.getJoinType() == JoinType.NONE || sqlTable.getJoinType() == JoinType.INNER) {
                sqlSimpleJoinTables.add(sqlTable);
                tables.remove(i);
                i--;
            }
        }

        return sqlSimpleJoinTables;
    }

    private LeftJoinTableColumnColumn createLeftJoinTable(SqlTable sqlTable, Join join) {
        Expression expression = join.getOnExpression();
        if (expression instanceof EqualsTo) {
            EqualsTo binaryExpression = (EqualsTo) expression;
            Expression leftExpression = binaryExpression.getLeftExpression();
            Expression rightExpression = binaryExpression.getRightExpression();
            if (leftExpression instanceof Column && rightExpression instanceof Column) {
                Column leftColumn = (Column) leftExpression;
                Column rightColumn = (Column) rightExpression;
                Column selfColumn = null;
                Column dependentColumn = null;

                SqlTable leftTable = tableManager.searchTableByColumn(leftColumn);
                SqlTable rightTable = tableManager.searchTableByColumn(rightColumn);
                if (leftTable == null) {
                    throw new IllegalArgumentException("Cannot find table for column [" + leftColumn + "] in join ON expression");
                }
                if (rightTable == null) {
                    throw new IllegalArgumentException("Cannot find table for column [" + rightColumn + "] in join ON expression");
                }
                if (leftTable == rightTable) {
                    throw new IllegalArgumentException("Join [" + join + "] has ON expression COLUMN=COLUMN with both columns from single table");
                }
                if (leftTable == sqlTable) {
                    selfColumn = leftColumn;
                    dependentColumn = rightColumn;
                } else if (rightTable == sqlTable) {
                    selfColumn = rightColumn;
                    dependentColumn = leftColumn;
                } else {
                    throw new IllegalArgumentException("Join expression [" + join + "] should have column from joined table");
                }

                String keyField = selfColumn.getColumnName();
                LeftJoinTableColumnColumn table = new LeftJoinTableColumnColumn(sqlTable, keyField, tableManager.searchTableByColumn(dependentColumn), dependentColumn.getColumnName());
                return table;
            } else {
                throw new IllegalArgumentException("Left join 'ON' expressions can have only COLUMN=COLUMN expression. Join expression: [" + join.toString() + "]");
            }
        } else {
            throw new IllegalArgumentException("Left join 'ON' expressions can have only COLUMN=COLUMN expression. Join expression: [" + join.toString() + "]");
        }
    }

}
