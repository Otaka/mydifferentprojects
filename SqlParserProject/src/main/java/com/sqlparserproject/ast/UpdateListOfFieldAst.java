package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class UpdateListOfFieldAst extends Ast {

    private final List<UpdateSetPairAst> fieldPairs = new ArrayList<>();

    public void add(UpdateSetPairAst updateSetPairAst) {
        fieldPairs.add(updateSetPairAst);
    }

    public List<UpdateSetPairAst> getFieldPairs() {
        return fieldPairs;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.printListOfAsts(fieldPairs, ",");
    }
}
