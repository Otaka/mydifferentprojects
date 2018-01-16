package com.sqlprocessor.sqlplan;

import com.sqlprocessor.compiler.SourceCode;
import com.sqlprocessor.table.TableManager;
import com.sqlprocessor.utils.StringBuilderWithPadding;

/**
 * @author sad
 */
public abstract class AbstractPlanItem {

    public void generateSourceCode(TableManager tableManager, StringBuilderWithPadding sourceCodeStringBuilder, SourceCode sourceCode) {
        sourceCodeStringBuilder.println("NOT IMPLEMENTED");
    }
}
