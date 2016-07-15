package com.sqlparserproject.ast;

import com.sqlparserproject.sqlformatter.SqlFormatterContext;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sad
 */
public class FunctionAst extends Ast {

    private String functionName;
    private List<Ast> arguments = new ArrayList<>();
    private OverPartitionByAst partitionBy;

    public FunctionAst(String functionName) {
        this.functionName = functionName;
    }

    public void setPartitionBy(OverPartitionByAst partitionBy) {
        this.partitionBy = partitionBy;
    }

    public OverPartitionByAst getPartitionBy() {
        return partitionBy;
    }

    public void addArgument(Ast ast) {
        arguments.add(ast);
    }

    public List<Ast> getArguments() {
        return arguments;
    }

    public String getFunctionName() {
        return functionName;
    }

    @Override
    public void formatSql(SqlFormatterContext context) {
        context.print(functionName).print("(").printListOfAsts(arguments, ",").print(")");
        if (partitionBy != null) {
            context.print(" OVER(").print(partitionBy).print(")");
        }
    }
}
