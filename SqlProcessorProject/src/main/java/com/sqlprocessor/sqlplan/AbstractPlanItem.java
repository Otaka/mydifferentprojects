package com.sqlprocessor.sqlplan;

import com.sqlprocessor.compiler.SourceCode;
import com.sqlprocessor.sqlplan.items.IterateOverTableLeftJoinPlanItem;
import com.sqlprocessor.sqlplan.items.IterateOverTablePlanItem;
import com.sqlprocessor.table.TableManager;
import com.sqlprocessor.utils.StringBuilderWithPadding;
import java.util.List;

/**
 * @author sad
 */
public abstract class AbstractPlanItem {

    public void generateSourceCode(TableManager tableManager, StringBuilderWithPadding sourceCodeStringBuilder, SourceCode sourceCode) {
        sourceCodeStringBuilder.println("NOT IMPLEMENTED");
    }
    
    public boolean isLastNestedLoop(List<AbstractPlanItem> childItems) {
        for (AbstractPlanItem childPlanItem : childItems) {
            if (childPlanItem instanceof IterateOverTablePlanItem ) {
                return false;
            }
        }

        return true;
    }
}
