package com.sqlprocessor.sqlplan.items;

import com.sqlprocessor.compiler.SourceCode;
import com.sqlprocessor.join.LeftJoinTableColumnColumn;
import com.sqlprocessor.sqlplan.AbstractPlanItem;
import com.sqlprocessor.table.TableManager;
import com.sqlprocessor.utils.StringBuilderWithPadding;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class IterateOverTableLeftJoinPlanItem extends AbstractPlanItem {

    private LeftJoinTableColumnColumn leftJoinObject;
    private List<AbstractPlanItem> childItems = new ArrayList<>();

    public IterateOverTableLeftJoinPlanItem(LeftJoinTableColumnColumn leftJoinObject) {
        this.leftJoinObject = leftJoinObject;
    }

    public IterateOverTableLeftJoinPlanItem addChildItems(List<? extends AbstractPlanItem> childPlanItems) {
        childItems.addAll(childPlanItems);
        return this;
    }

    public IterateOverTableLeftJoinPlanItem addChildItems(AbstractPlanItem childPlanItem) {
        childItems.add(childPlanItem);
        return this;
    }

    public List<AbstractPlanItem> getChildItems() {
        return childItems;
    }

    public LeftJoinTableColumnColumn getLeftJoinObject() {
        return leftJoinObject;
    }

    @Override
    public String toString() {
        return leftJoinObject != null ? leftJoinObject.toString() : "null";
    }

    @Override
    public void generateSourceCode(TableManager tableManager, StringBuilderWithPadding sourceCode, SourceCode sourceCodeGenerator) {
        int tableIndex = leftJoinObject.getTable().getId();
        String dataWithIndex = "data" + tableIndex;
        String indexVariable = dataWithIndex + "_i";
        String tableSizeVariable = dataWithIndex + "_size";
        int tmpFieldIndex = sourceCodeGenerator.getNextFreeId();
        String dependentTableRow = "combinedWorkingTable.data" + leftJoinObject.getDependentTable().getId();
        sourceCode.println("//left join for table " + dataWithIndex);
        sourceCode.println("int " + tableSizeVariable + " = " + dataWithIndex + "_list.size();");
        sourceCode.println("combinedWorkingTable." + dataWithIndex + " = null;");
        sourceCode.println("for(int " + indexVariable + " = 0; " + indexVariable + " < " + tableSizeVariable + "; " + indexVariable + "++) {");
        sourceCode.incLevel();
        String tmpFieldWithJoinedTableRow = "tmpField" + tmpFieldIndex;
        sourceCode.println(leftJoinObject.getTable().getBuffer().getDataClass().getName() + " " + tmpFieldWithJoinedTableRow + " = " + dataWithIndex + "_list.get(" + indexVariable + ");");

        sourceCode.println("if(" + tmpFieldWithJoinedTableRow + "==null||" + dependentTableRow + "==null){continue;}");
        sourceCode.println("if(objectEquals(" + tmpFieldWithJoinedTableRow + "." + leftJoinObject.getKeyField() + "," + dependentTableRow + "." + leftJoinObject.getDependentField() + ")){"); //TODO: make different comparing dependent of field type
        sourceCode.incLevel();
        sourceCode.println("combinedWorkingTable." + dataWithIndex + " = " + tmpFieldWithJoinedTableRow + ";");
        sourceCode.println("break;");
        sourceCode.decLevel();
        sourceCode.println("}");

        sourceCode.decLevel();
        sourceCode.append("}\n");

        for (AbstractPlanItem childPlanItem : childItems) {
            childPlanItem.generateSourceCode(tableManager, sourceCode, sourceCodeGenerator);
        }
    }
}
