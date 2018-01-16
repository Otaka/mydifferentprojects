package com.sqlprocessor.sqlplan.items;

import com.sqlprocessor.join.LeftJoinTableColumnColumn;
import com.sqlprocessor.sqlplan.AbstractPlanItem;

/**
 * @author sad
 */
public class IterateOverTableLeftJoinPlanItem extends AbstractPlanItem {

    private LeftJoinTableColumnColumn leftJoinObject;

    public IterateOverTableLeftJoinPlanItem(LeftJoinTableColumnColumn leftJoinObject) {
        this.leftJoinObject = leftJoinObject;
    }

    public LeftJoinTableColumnColumn getLeftJoinObject() {
        return leftJoinObject;
    }

    @Override
    public String toString() {
        return leftJoinObject != null ? leftJoinObject.toString() : "null";
    }
}
