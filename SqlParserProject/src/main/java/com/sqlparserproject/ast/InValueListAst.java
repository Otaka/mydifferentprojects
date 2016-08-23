package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class InValueListAst extends Ast {

    private List<String> variants = new ArrayList<String>();
    
    public void add(String value) {
        variants.add(value);
    }

    public List<String> getVariants() {
        return variants;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.printList(variants, ",");
    }
}
