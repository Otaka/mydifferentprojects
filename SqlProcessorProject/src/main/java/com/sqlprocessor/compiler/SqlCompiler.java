package com.sqlprocessor.compiler;

import com.sqlprocessor.compiler.exception.CannotCompileClassException;
import com.sqlprocessor.table.TableManager;
import com.sqlprocessor.buffers.SqlBuffer;
import com.sqlprocessor.sqlplan.AbstractPlanItem;
import com.sqlprocessor.sqlplan.CombinedWorkingTable;
import com.sqlprocessor.sqlplan.ExecutionPlan;
import com.sqlprocessor.table.SqlField;
import com.sqlprocessor.table.SqlTable;
import com.sqlprocessor.utils.StringBuilderWithPadding;
import com.sqlprocessor.utils.StringBuilderWithSeparator;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.openhft.compiler.CompilerUtils;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

/**
 * @author sad
 */
public class SqlCompiler {

    private TableManager tableManager = new TableManager();
    private PlainSelect plainSelect;
    private static AtomicInteger tableIndexCounter = new AtomicInteger(0);
    private static AtomicInteger classesCounter = new AtomicInteger(0);

    public CompiledSql compileSql(String sql, SqlBuffer... buffers) throws JSQLParserException {
        checkBuffersNotHaveDuplicateNames(buffers);
        Statements statements = CCJSqlParserUtil.parseStatements(sql);
        plainSelect = extractPlainSelectStatement(statements);
        tableManager.registerBuffers(buffers);
        tableManager.collectTableNames(plainSelect);
        ExecutionPlanProcessor executionPlanProcessor = new ExecutionPlanProcessor(tableManager);
        ExecutionPlan plan = executionPlanProcessor.createExecutionPlan(plainSelect);
        Class combinedWorkingTable = createCombinedWorkingTable(tableManager);
        SourceCode sourceCode = new SourceCode();
        createSqlTableFields(tableManager, sourceCode);
        StringBuilderWithPadding collectedInnerLoopSourceCodeSB = new StringBuilderWithPadding();
        generateProcessTableLoopStage(tableManager, plan, collectedInnerLoopSourceCodeSB, sourceCode);
        List<GeneratedOutputField> generatedOutputFields = generateSelectOutputFields(tableManager, sourceCode, plainSelect.getSelectItems());
        Class outputRowClass = generateOutputRowClass(generatedOutputFields);
        String processMethodSourceCode = collectedInnerLoopSourceCodeSB.toString();
        sourceCode.addInitializationSourceCode("private List<" + outputRowClass.getName() + ">finalResultList = new ArrayList<" + outputRowClass.getName() + ">();");
        if (!hasGroupBy()) {
            sourceCode.addInitializationSourceCode("private ArrayList<" + tableManager.getCombinedWorkingTableRowClassName() + ">collectedData = new ArrayList<>(1000);");
            String emitLineSourceCode = "collectedData.add(combinedWorkingTable.copy());";
            processMethodSourceCode = processMethodSourceCode.replace("&{emmitLine}", emitLineSourceCode);
            if (hasOrderBy()) {
                StringBuilderWithPadding orderBySB = createOrderByForCombinedWorkingTable(tableManager, sourceCode, plainSelect.getOrderByElements());
                String orderBySourceCode = orderBySB.toString();
                processMethodSourceCode = processMethodSourceCode + "\n" + orderBySourceCode;
            }

            //copy to output list
            StringBuilderWithPadding sb = new StringBuilderWithPadding();
            sb.println("for(" + combinedWorkingTable.getName() + " workingRow:collectedData) {");
            sb.incLevel();
            sb.println(generateCopyFromCombinedWorkingRowToOutputRow(outputRowClass, generatedOutputFields, tableManager, sourceCode));
            sb.println("finalResultList.add(outputDataRow);");
            sb.decLevel();
            sb.println("}");
            sb.println("collectedData.clear();");

            processMethodSourceCode = processMethodSourceCode + "\n" + sb.toString();
        } else {
            throw new IllegalStateException("Group by expressions is not implemented yet");
        }

        String createOutputSqlBufferMethod = generateOutputSqlBufferMethod(outputRowClass, generatedOutputFields, sourceCode);

        StringBuilderWithPadding fullSourceCode = new StringBuilderWithPadding();
        String processorPackage = "com.sqlprocessor.sqlprocessor";
        String processorClass = "SqlProcessor_" + classesCounter.incrementAndGet();

        fullSourceCode.print("package ").print(processorPackage).println(";");
        fullSourceCode.println();
        fullSourceCode.println("import java.util.*;");
        fullSourceCode.println("import " + SqlBuffer.class.getName() + ";");
        fullSourceCode.println("import " + combinedWorkingTable.getName() + ";");
        fullSourceCode.println("import " + outputRowClass.getName() + ";");
        
        fullSourceCode.println("\n");
        fullSourceCode.print("public class ").print(processorClass).print(" extends ").print(SqlExecutor.class.getName()).println(" {");
        fullSourceCode.incLevel();
        for (String line : sourceCode.getInitializationSourceCode()) {
            fullSourceCode.println(line);
        }

        fullSourceCode.println();

        fullSourceCode.print(createSetSqlBufferMethod(tableManager, sourceCode).toString());

        fullSourceCode.println("@Override");
        fullSourceCode.println("public void process() {");
        fullSourceCode.incLevel();
        fullSourceCode.println("finalResultList.clear();");
        fullSourceCode.println("if(outputSqlBuffer==null){throw new IllegalStateException(\"outputSqlBuffer is not generated. Please execute createOutputSqlBuffer first\");}");
        fullSourceCode.println("outputSqlBuffer.setData(finalResultList);");
        fullSourceCode.println(removeNewLine(processMethodSourceCode));
        fullSourceCode.decLevel();
        fullSourceCode.println("}");

        fullSourceCode.println(removeNewLine(createOutputSqlBufferMethod));
        fullSourceCode.decLevel();
        fullSourceCode.println("}");

        Class sqlExecutorClass = compileClass(processorPackage, processorClass, fullSourceCode.toString());
        CompiledSql compiledSql = new CompiledSql(sqlExecutorClass, buffers);
        return compiledSql;
    }

    public String removeNewLine(String line) {
        return StringUtils.stripEnd(line, "\n\r \t");
    }

    private String generateOutputSqlBufferMethod(Class outputRowClass, List<GeneratedOutputField> outputRowFields, SourceCode sourceCode) {
        sourceCode.addInitializationSourceCode("private SqlBuffer outputSqlBuffer=null;");
        StringBuilderWithPadding sb = new StringBuilderWithPadding();
        sb.println("@Override");
        sb.println("public SqlBuffer createOutputSqlBuffer(String name) {");
        sb.incLevel();
        sb.println("if(outputSqlBuffer!=null){return outputSqlBuffer;}");
        sb.println("List<String>outputFields=new ArrayList<>();");
        for (GeneratedOutputField generatedField : outputRowFields) {
            sb.println("outputFields.add(\"" + StringEscapeUtils.escapeJava(generatedField.getName()) + "\");");
        }

        sb.println("outputSqlBuffer = new SqlBuffer(name," + outputRowClass.getName() + ".class, outputFields);");
        sb.println("return outputSqlBuffer;");
        sb.decLevel();
        sb.println("}");
        return sb.toString();
    }

    private String generateCopyFromCombinedWorkingRowToOutputRow(Class outputRowClass, List<GeneratedOutputField> generatedOutputFields, TableManager tableManager, SourceCode sourceCode) {
        StringBuilderWithPadding sb = new StringBuilderWithPadding();
        sb.println(outputRowClass.getName() + " outputDataRow=new " + outputRowClass.getName() + "(");

        boolean first = true;
        for (GeneratedOutputField field : generatedOutputFields) {
            if (first == false) {
                sb.println(",");
            }

            sb.print("(" + removeNewLine(field.getSourceCode()) + ")");
            first = false;
        }

        sb.print(");");
        return sb.toString();
    }

    private Class generateOutputRowClass(List<GeneratedOutputField> generateSelectOutputFields) {
        String className = "OutputRow" + tableIndexCounter.incrementAndGet();
        String packagePath = "com.sqlprocessor.dataclasses";
        StringBuilderWithPadding sb = new StringBuilderWithPadding();
        sb.println("package " + packagePath + ";");
        sb.println("public class " + className + " {");
        sb.incLevel();

        //generate fields
        for (GeneratedOutputField field : generateSelectOutputFields) {
            sb.println("public " + field.getType().getName() + " " + field.getName() + ";");
        }
        sb.println();

        //generate constructor
        StringBuilderWithSeparator argsListSb = new StringBuilderWithSeparator(",");
        for (GeneratedOutputField field : generateSelectOutputFields) {
            argsListSb.append(field.getType().getName() + " " + field.getName());
            argsListSb.newEntry();
        }

        sb.println("public " + className + "(" + argsListSb.toString() + ") {");
        sb.incLevel();

        for (GeneratedOutputField field : generateSelectOutputFields) {
            sb.println("this." + field.getName() + " = " + field.getName() + ";");
        }

        sb.decLevel();
        sb.println("}");
        sb.println();

        //generate getters
        for (GeneratedOutputField field : generateSelectOutputFields) {
            sb.println("public " + field.getType().getName() + " get" + StringUtils.capitalize(field.getName()) + "(){return " + field.getName() + ";}");
        }

        sb.decLevel();
        sb.print("}");
        return compileClass(packagePath, className, sb.toString());
    }

    private void createSqlTableFields(TableManager tableManager, SourceCode sourceCode) {
        for (SqlTable sqlTable : tableManager.getSqlTables()) {
            sourceCode.addInitializationSourceCode("private List<" + sqlTable.getBuffer().getDataClass().getName() + "> data" + sqlTable.getId() + "_list;");
        }
    }

    private StringBuilderWithPadding createSetSqlBufferMethod(TableManager tableManager, SourceCode sourceCode) {
        StringBuilderWithPadding sb = new StringBuilderWithPadding();
        sb.println("@Override");
        sb.println("public void setSqlBuffer(com.sqlprocessor.buffers.SqlBuffer sqlBuffer) {");
        sb.incLevel();

        for (SqlBuffer sqlBuffer : tableManager.getBuffers()) {
            String bufferName = sqlBuffer.getName();
            sb.println("if(\"" + StringEscapeUtils.escapeJava(bufferName) + "\".equals(sqlBuffer.getName())) {");
            sb.incLevel();
            for (SqlTable sqlTable : tableManager.getSqlTables()) {
                if (sqlTable.getBuffer() == sqlBuffer) {
                    sb.println("data" + sqlTable.getId() + "_list=(List<" + sqlBuffer.getDataClass().getName() + ">)sqlBuffer.getData();");
                }
            }

            sb.println("return;");

            sb.decLevel();
            sb.println("}");
        }

        sb.println("throw new IllegalArgumentException(\"The SqlProcessor cannot work with [\"+sqlBuffer.getName()+\"] SqlBuffer\");");
        sb.decLevel();
        sb.println("}");
        return sb;
    }

    private List<GeneratedOutputField> generateSelectOutputFields(TableManager tableManager, SourceCode sourceCode, List<SelectItem> selectItems) {
        List<GeneratedOutputField> generatedOutputFields = new ArrayList<>();
        for (int i = 0; i < selectItems.size(); i++) {
            SelectItem si = selectItems.get(i);
            si.accept(new SelectItemVisitor() {
                @Override
                public void visit(AllColumns allColumns) {
                    for (SqlTable table : tableManager.getSqlTables()) {
                        for (String field : (List<String>) table.getBuffer().getFields()) {
                            String newOutputFieldName = chooseNewNameForField(generatedOutputFields, field);
                            String fieldNameToRead = "_data" + table.getId() + "_" + field + "()";
                            StringBuilderWithPadding sb = new StringBuilderWithPadding();
                            sb.print("workingRow." + fieldNameToRead);

                            GeneratedOutputField outputField = new GeneratedOutputField(table.getBuffer().getField(field).getType(), newOutputFieldName,
                                    sb.toString());
                            generatedOutputFields.add(outputField);
                        }
                    }
                }

                @Override
                public void visit(AllTableColumns allTableColumns) {
                    String tableAlias = tableManager.getTableAlias(allTableColumns.getTable());
                    SqlTable table = tableManager.searchTableByAlias(tableAlias);
                    for (String field : (List<String>) table.getBuffer().getFields()) {
                        String newOutputFieldName = chooseNewNameForField(generatedOutputFields, field);
                        String fieldNameToRead = "_data" + table.getId() + "_" + field + "()";
                        StringBuilderWithPadding sb = new StringBuilderWithPadding();
                        sb.print("workingRow." + fieldNameToRead);

                        GeneratedOutputField outputField = new GeneratedOutputField(
                                table.getBuffer().getField(field).getType(),
                                newOutputFieldName,
                                sb.toString());
                        generatedOutputFields.add(outputField);
                    }
                }

                @Override
                public void visit(SelectExpressionItem selectExpressionItem) {
                    ExpressionExecutor.ExpressionExecutorResult expressionExecutorResult = new ExpressionExecutor().executeExpression(selectExpressionItem.getExpression(), tableManager, sourceCode,"workingRow");
                    String fieldName;
                    if (selectExpressionItem.getAlias() != null) {
                        fieldName = selectExpressionItem.getAlias().getName();
                    } else {
                        fieldName = expressionExecutorResult.resultType.getSimpleName().toLowerCase();
                    }

                    fieldName = chooseNewNameForField(generatedOutputFields, fieldName);
                    GeneratedOutputField outputField = new GeneratedOutputField(expressionExecutorResult.resultType, fieldName, expressionExecutorResult.expressionExecSourceCode);
                    generatedOutputFields.add(outputField);
                }
            });
        }

        return generatedOutputFields;
    }

    private String chooseNewNameForField(List<GeneratedOutputField> generatedOutputFields, String fieldBaseName) {
        if (!isListContainsFieldNameIgnoreCase(generatedOutputFields, fieldBaseName)) {
            return fieldBaseName;
        }

        for (int i = 1; i < 10000; i++) {
            if (!isListContainsFieldNameIgnoreCase(generatedOutputFields, fieldBaseName + i)) {
                return fieldBaseName + i;
            }
        }

        throw new IllegalStateException("For some reason cannot choose new field name for field [" + fieldBaseName + "]");
    }

    private boolean isListContainsFieldNameIgnoreCase(List<GeneratedOutputField> fields, String str) {
        for (GeneratedOutputField value : fields) {
            if (value.getName().equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

    private StringBuilderWithPadding createOrderByForCombinedWorkingTable(TableManager tableManager, SourceCode sourceCode, List<OrderByElement> orderByElements) {
        StringBuilderWithPadding sb = new StringBuilderWithPadding();
        sb.append("collectedData.sort(new Comparator<").append(tableManager.getCombinedWorkingTableRowClassName()).println(">() {");
        sb.incLevel();

        sb.println("@Override");
        sb.print("public int compare(").append(tableManager.getCombinedWorkingTableRowClassName()).append(" o1,").append(tableManager.getCombinedWorkingTableRowClassName()).println(" o2) {");
        sb.incLevel();

        for (int i = 0; i < orderByElements.size(); i++) {
            OrderByElement orderByElement = orderByElements.get(i);
            if (!(orderByElement.getExpression() instanceof Column)) {
                throw new IllegalArgumentException("Order by expression [" + orderByElement.getExpression().getClass().getSimpleName() + "] is not supported [" + orderByElement.getExpression() + "]. Only column expressions are supported");
            }

            SqlField field = tableManager.getSqlFieldByColumn((Column) orderByElement.getExpression());
            boolean isAsc = orderByElement.isAsc();
            Field f = field.getTable().getBuffer().getField(field.getField());
            Class type = f.getType();
            String fieldName = "_data" + field.getTable().getId() + "_" + field.getField() + "()";
            String o1 = "o1." + fieldName;
            String o2 = "o2." + fieldName;
            String negative = isAsc ? "-1" : "1";
            String positive = isAsc ? "1" : "-1";
            if (ExpressionExecutor.isInt(type) || ExpressionExecutor.isLong(type) || ExpressionExecutor.isDouble(type)) {
                sb.println("if(" + o1 + " < " + o2 + "){return " + negative + ";}");
                sb.println("if(" + o1 + " > " + o2 + "){return " + positive + ";}");
            } else if (ExpressionExecutor.isString(type)) {
                sb.println("int compareResult" + i + " = " + o1 + ".compareTo(" + o2 + ");");
                sb.println("if(compareResult" + i + "<0){return " + negative + ";}");
                sb.println("if(compareResult" + i + ">0){return " + positive + ";}");
            } else if (ExpressionExecutor.isBoolean(type)) {
                sb.println("if(" + o1 + "!=" + o2 + "){");
                sb.incLevel();
                sb.println("if(" + o1 + "== false){return " + negative + ";}");
                sb.println("if(" + o1 + "== true){return " + positive + ";}");
                sb.decLevel();
                sb.println("}");
            } else {
                throw new IllegalArgumentException("You cannot do order by for type [" + type.getSimpleName() + "]. Field " + field);
            }
        }

        sb.println("return 0;");

        sb.decLevel();
        sb.println("}");
        sb.decLevel();
        sb.println("});");
        return sb;
    }

    private boolean hasOrderBy() {
        return !(plainSelect.getOrderByElements() == null || plainSelect.getOrderByElements().isEmpty());
    }

    private boolean hasGroupBy() {
        return !(plainSelect.getGroupByColumnReferences() == null || plainSelect.getGroupByColumnReferences().isEmpty());
    }

    private void generateProcessTableLoopStage(TableManager tableManager, ExecutionPlan executionPlan, StringBuilderWithPadding collectedSourceCodeSB, SourceCode sourceCode) {
        collectedSourceCodeSB.println(tableManager.getCombinedWorkingTableRowClassName()+" combinedWorkingTable=new "+tableManager.getCombinedWorkingTableRowClassName()+"();");
        for (AbstractPlanItem planItem : executionPlan.getStage1LoopPlanItems()) {
            planItem.generateSourceCode(tableManager, collectedSourceCodeSB, sourceCode);
        }
    }

    private Class createCombinedWorkingTable(TableManager tableManager) {
        StringBuilderWithPadding sb = new StringBuilderWithPadding();
        String packagePath = "com.sqlprocessor.dataclasses";
        String className = "CombinedWorkingTable_" + classesCounter.incrementAndGet();
        tableManager.setCombinedWorkingTableRowClassName(className);
        sb.append("package ").append(packagePath).append(";\n\n");
        sb.append("public class ").append(className).append(" extends ").append(CombinedWorkingTable.class.getName()).append(" {\n");
        sb.incLevel();
        for (SqlTable table : tableManager.getSqlTables()) {
            List<String> fieldsNames = table.getBuffer().getFields();
            String generatedDataObjectFieldName = "data" + table.getId();
            sb.append("public ").append(table.getBuffer().getDataClass().getName()).append(" ").append(generatedDataObjectFieldName).append(";\n");
            for (String fieldName : fieldsNames) {
                Field reflectionField = table.getBuffer().getField(fieldName);
                String generatedFieldName = "_" + generatedDataObjectFieldName + "_" + fieldName;
                sb.append("public ").append(reflectionField.getType().getName()).append(" ").append(generatedFieldName).append("() {\n");
                sb.incLevel();
                sb.append("return ").append(generatedDataObjectFieldName).append(".").append(table.getBuffer().getGetterMethod(fieldName)).append("();\n");
                sb.decLevel();
                sb.append("}\n");
            }
        }

        sb.append("public ").append(className).println(" copy(){");
        sb.incLevel();
        sb.append(className).append(" newObject = new ").append(className).println("();");
        for (int i = 0; i < tableManager.getSqlTables().size(); i++) {
            SqlTable table = tableManager.getSqlTables().get(i);
            sb.append("newObject.data").append(table.getId()).append(" = data").append(table.getId()).println(";");
        }
        sb.println("return newObject;");

        sb.decLevel();
        sb.println("}");
        sb.decLevel();
        sb.append("}");
        return compileClass(packagePath, className, sb.toString());
    }

    public static Class compileClass(String packagePath, String className, String sourceCode) {
        try {
            Class resultClazz = CompilerUtils.CACHED_COMPILER.loadFromJava(packagePath + "." + className, sourceCode);
            return resultClazz;
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            throw new CannotCompileClassException("Internal exception. Cannot compile CombinedWorkingTable class [\n" + sourceCode + "\n]", ex);
        }
    }

    private void checkBuffersNotHaveDuplicateNames(SqlBuffer[] buffers) {
        Set<String> bufferNames = new HashSet<>();
        for (SqlBuffer buffer : buffers) {
            String name = buffer.getName().toLowerCase();
            if (bufferNames.contains(name)) {
                throw new IllegalArgumentException("You have provided two buffers with the same name");
            }

            bufferNames.add(name);
        }
    }

    private PlainSelect extractPlainSelectStatement(Statements sqlExpression) {
        for (Statement statement : sqlExpression.getStatements()) {
            if (!(statement instanceof Select)) {
                throw new UnsupportedOperationException("Supported only regular select queries but found " + statement.getClass().getName());
            }

            Select selectQuery = (Select) statement;
            return (PlainSelect) (selectQuery.getSelectBody());
        }

        throw new IllegalArgumentException("Your expression [" + sqlExpression.toString() + "] does not have select statement");
    }
}
