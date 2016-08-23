package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class FieldListAST extends Ast {

    private List<Ast> fields = new ArrayList<>();

    public List<Ast> getFields() {
        return fields;
    }

    public Ast getField(int index) {
        return fields.get(index);
    }

    public void addField(Ast field) {
        fields.add(field);
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.printListOfAsts(fields, ", ");
    }
}
