package com.sqlprocessor.sqlplan;

import com.sqlprocessor.table.SqlField;
import java.util.List;

/**
 * @author sad
 */
public class ExecutionPlan {

    private List<AbstractPlanItem> stage1LoopPlanItems;
    private List<AbstractPlanItem> stage2GroupByPlanItems;
    private List<AbstractPlanItem> stage3SortPlanItems;

    private List<SqlField> groupingFields;

    public void setGroupingFields(List<SqlField> groupingFields) {
        this.groupingFields = groupingFields;
    }

    public List<AbstractPlanItem> getStage1LoopPlanItems() {
        return stage1LoopPlanItems;
    }

    public List<AbstractPlanItem> getStage2GroupByPlanItems() {
        return stage2GroupByPlanItems;
    }

    public List<AbstractPlanItem> getStage3SortPlanItems() {
        return stage3SortPlanItems;
    }

    public void setStage1LoopPlanItems(List<AbstractPlanItem> stage1LoopPlanItems) {
        this.stage1LoopPlanItems = stage1LoopPlanItems;
    }

    public void setStage2GroupByPlanItems(List<AbstractPlanItem> stage2GroupByPlanItems) {
        this.stage2GroupByPlanItems = stage2GroupByPlanItems;
    }

    public void setStage3SortPlanItems(List<AbstractPlanItem> stage3SortPlanItems) {
        this.stage3SortPlanItems = stage3SortPlanItems;
    }

    public List<SqlField> getGroupingFields() {
        return groupingFields;
    }

}
