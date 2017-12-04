package com.sqlprocessor.sqlplan.items;

import com.sqlprocessor.sqlplan.AbstractPlanItem;
import net.sf.jsqlparser.expression.Expression;

/**
 * @author sad
 */
public class HavingPlanItem extends AbstractPlanItem {

    private Expression filterExpression;

    public HavingPlanItem(Expression filterExpression) {
        this.filterExpression = filterExpression;
    }

    public Expression getFilterExpression() {
        return filterExpression;
    }

}
