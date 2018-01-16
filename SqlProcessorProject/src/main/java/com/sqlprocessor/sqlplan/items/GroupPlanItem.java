package com.sqlprocessor.sqlplan.items;

import com.sqlprocessor.sqlplan.AbstractPlanItem;
import com.sqlprocessor.table.SqlField;
import java.util.List;

/**
 * @author sad
 */
public class GroupPlanItem extends AbstractPlanItem {

    private List<SqlField> groupingFields;

    public GroupPlanItem(List<SqlField> groupingFields) {
        this.groupingFields = groupingFields;
    }

    public List<SqlField> getGroupingFields() {
        return groupingFields;
    }

}
