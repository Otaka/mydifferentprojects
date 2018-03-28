package com.sqlprocessor.sqlplan.items;

import com.sqlprocessor.compiler.SourceCode;
import com.sqlprocessor.sqlplan.AbstractPlanItem;
import com.sqlprocessor.table.SqlTable;
import com.sqlprocessor.table.TableManager;
import com.sqlprocessor.utils.StringBuilderWithPadding;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class IterateOverTablePlanItem extends AbstractPlanItem {

    private List<AbstractPlanItem> childItems = new ArrayList<>();
    private SqlTable sqlTable;

    public IterateOverTablePlanItem(SqlTable table) {
        this.sqlTable = table;
    }

    public IterateOverTablePlanItem addChildItems(List<? extends AbstractPlanItem> childPlanItems) {
        childItems.addAll(childPlanItems);
        return this;
    }

    public IterateOverTablePlanItem addChildItems(AbstractPlanItem childPlanItem) {
        childItems.add(childPlanItem);
        return this;
    }

    public List<AbstractPlanItem> getChildItems() {
        return childItems;
    }

    public SqlTable getSqlTable() {
        return sqlTable;
    }

    public void setChildItems(List<AbstractPlanItem> childItems) {
        this.childItems = childItems;
    }

    @Override
    public String toString() {
        return sqlTable != null ? sqlTable.toString() : "null table";
    }

    @Override
    public void generateSourceCode(TableManager tableManager, StringBuilderWithPadding sourceCode, SourceCode sourceCodeGenerator) {
        int tableIndex = sqlTable.getId();
        String dataWithIndex = "data" + tableIndex;
        String indexVariable = dataWithIndex + "_i";
        String tableSizeVariable = dataWithIndex + "_size";

        sourceCode.println("int " + tableSizeVariable + " = " + dataWithIndex + "_list.size();");
        sourceCode.println("for(int " + indexVariable + " = 0; " + indexVariable + " < " + tableSizeVariable + "; " + indexVariable + "++) {");
        sourceCode.incLevel();
        sourceCode.println("combinedWorkingTable." + dataWithIndex + " = " + dataWithIndex + "_list.get(" + indexVariable + ");");
        for (AbstractPlanItem childPlanItem : childItems) {
            childPlanItem.generateSourceCode(tableManager, sourceCode, sourceCodeGenerator);
        }

        if (isLastNestedLoop(childItems)) {
            sourceCode.println("&{emmitLine}");
        }

        sourceCode.decLevel();
        sourceCode.append("}\n");
    }

    
}
