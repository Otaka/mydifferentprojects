package com.sqlprocessor.sqlplan.items;

import com.sqlprocessor.sqlplan.AbstractPlanItem;
import com.sqlprocessor.table.SqlSortField;
import java.util.List;

/**
 * @author sad
 */
public class SortPlanItem extends AbstractPlanItem {

    private List<SqlSortField> fields;

    public SortPlanItem(List<SqlSortField> fields) {
        this.fields = fields;
    }

    public List<SqlSortField> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return fields!=null? fields.toString():"empty";
    }
    
    
}
