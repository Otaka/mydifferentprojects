package com.sqlparserproject.sqlformatter;

import com.sqlparserproject.ast.Ast;
import java.util.List;

/**
 * @author sad
 */
public class SqlFormatterContext {

    private StringBuilder sb = new StringBuilder(500);

    public SqlFormatterContext print(String value) {
        sb.append(value);
        return this;
    }

    public SqlFormatterContext print(Ast ast) {
        ast.formatSql(this);
        return this;
    }

    public SqlFormatterContext newLine() {
        sb.append("\n");
        return this;
    }

    public String getResult() {
        return sb.toString();
    }

    public SqlFormatterContext printListOfAsts(List<? extends Ast> asts, String separator) {
        boolean first = true;
        for (Ast ast : asts) {
            if (!first) {
                sb.append(separator);
            }

            ast.formatSql(this);
            first = false;
        }

        return this;
    }
    
    public SqlFormatterContext printList(List values, String separator) {
        boolean first = true;
        for (Object value : values) {
            if (!first) {
                sb.append(separator);
            }

            sb.append(value);
            first = false;
        }

        return this;
    }

    public static boolean stringNotEmpty(String str) {
        return !(str == null || str.trim().isEmpty());

    }
}
