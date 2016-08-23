package com.sqlparserproject;

import com.sqlparserproject.ast.OrderByAst;
import com.sqlparserproject.ast.*;
import com.sqlparserproject.ast.helperobjects.SectionObj;
import com.sqlparserproject.ast.helperobjects.StringObj;
import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.errors.ParsingException;
import org.parboiled.support.Position;

/**
 * @author sad
 */
public class SqlParserActions extends BaseParser {

    public static String TYPE_CONVERSION = "typeConversion";
    public static boolean testDisableActions = false;

    public static abstract class SqlAction implements Action {

        @Override
        public boolean run(Context context) {
            if (testDisableActions) {
                return true;
            }

            return runAction(context);
        }

        public abstract boolean runAction(Context context);
    }

    public Action actionFail(final String message) {
        return new Action() {
            @Override
            public boolean run(Context context) {
                String m = message;
                if (m.contains("$match")) {
                    m = m.replace("$match", context.getMatch());
                }

                int currentIndex = context.getCurrentIndex();
                Position position = context.getInputBuffer().getPosition(currentIndex);
                String part = context.getInputBuffer().extract(currentIndex, currentIndex + 20);
                m = "[Line:" + position.line + " Column:" + position.column + "]" + m + " . Found \"" + part + "\"";
                throw new ParsingException(m);
            }
        };
    }
   /*
    public Action dbgPrint(final String message) {
        return new Action() { 
            @Override
            public boolean run(Context context) {
                String m = message;
                if (m.contains("$match")) {
                    m = m.replace("$match", context.getMatch());
                }

                System.out.println(m);
                return true;
            }
        };
    }*/

    public Action pushSqlListAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                push(new SqlListAst());
                return true;
            }
        };
    }

    public Action putSqlToSqlListAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast sqlExpression = (Ast) pop();
                SqlListAst sqlList = (SqlListAst) peek();
                sqlList.add(sqlExpression);
                return true;
            }
        };
    }

    public Action pushTypeConversionAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                SqlType type = (SqlType) pop();
                Ast ast = (Ast) pop();
                TypeConversionAst conversion = new TypeConversionAst(ast, type);
                push(conversion);
                return true;
            }
        };
    }

    public Action pushDropTableAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                push(new DropTableAST());
                return true;
            }
        };
    }

    public Action pushTableNameAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                push(new TableNameAst(lastMatch()));
                return true;
            }
        };
    }

    public Action pushHelperString(final String label) {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                String value = context.getMatch().trim();
                push(new StringObj(value, label));
                return true;
            }
        };
    }

    public Action pushBinaryOperationRule() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast rightAst = (Ast) pop();
                StringObj operation = (StringObj) pop();
                Ast leftAst = (Ast) pop();
                push(new BinaryOperation(operation.getVal(), leftAst, rightAst));
                return true;
            }
        };
    }

    public String popHelperString(String label) {
        StringObj string = (StringObj) pop();
        if (!string.getLabel().equals(label)) {
            throw new RuntimeException("On the top of the stack found helperString '" + string.getLabel() + "' but expected '" + label + "'");
        }

        return string.getVal();
    }

    public Action pushExistsAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast lastAst = (Ast) pop();
                push(new ExistsAst(lastAst));
                return true;
            }
        };
    }

    public Action pushInFromSelectAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                SelectAst selectAst = (SelectAst) pop();
                SectionObj section = popSectionFromTop("in_section");
                SimpleFieldAst readField = (SimpleFieldAst) pop();
                InFromSelectAst ast = new InFromSelectAst(readField, selectAst);
                ast.setNot(section.contains("not"));
                push(ast);
                return true;
            }
        };
    }

    public Action pushInFromValueListAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                InValueListAst valueListAst = (InValueListAst) pop();
                SectionObj section = popSectionFromTop("in_section");
                Ast readField = (Ast) pop();
                InFromValueListAst ast = new InFromValueListAst(readField, valueListAst);
                ast.setNot(section.contains("not"));

                push(ast);
                return true;
            }
        };
    }

    public Action addValueToInValueList() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Object obj = pop();
                InValueListAst valueList = (InValueListAst) peek();
                String value = null;
                if (obj instanceof NumberAst) {
                    value = ((NumberAst) obj).getValue();
                } else if (obj instanceof StringAst) {
                    value = "'" + ((StringAst) obj).getValue() + "'";
                } else {
                    throw new RuntimeException("Cannot process [" + obj.getClass() + "] expected only NumberAst or StringAst");
                }
                valueList.add(value);
                return true;
            }
        };
    }

    public Action pushInValueList() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                push(new InValueListAst());
                return true;
            }
        };
    }

    public Action pushExtractFromEpochAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast ast = (Ast) pop();
                push(new ExtractEpochFromAst(ast));
                return true;
            }
        };
    }

    public Action pushReadingFieldWithTableAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                StringObj fieldName = (StringObj) pop();
                StringObj tableName = (StringObj) pop();
                push(new SimpleFieldAst(tableName.getVal(), fieldName.getVal()));
                return true;
            }
        };
    }

    public Action pushReadingFieldWithoutTableAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                StringObj fieldName = (StringObj) pop();
                push(new SimpleFieldAst(null, fieldName.getVal()));
                return true;
            }
        };
    }

    public Action pushOrder_GroupByFieldAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast ast = (Ast) pop();
                OrderGroupByFieldAst fieldAst = new OrderGroupByFieldAst();
                fieldAst.setField(ast);
                push(fieldAst);
                return true;
            }
        };
    }

    public Action pushOrderGroupByFieldListAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                OrderGroupByColumnListAst fieldsAst = new OrderGroupByColumnListAst();
                push(fieldsAst);
                return true;
            }
        };
    }

    public Action putOrderGroupByFieldToListAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                OrderGroupByFieldAst field = (OrderGroupByFieldAst) pop();
                OrderGroupByColumnListAst fieldsAst = (OrderGroupByColumnListAst) peek();
                fieldsAst.add(field);
                return true;
            }
        };
    }

    public Action putOrderGroupByFieldListToOrderByAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                OrderGroupByColumnListAst fieldList = (OrderGroupByColumnListAst) pop();
                OrderByAst orderByAst = (OrderByAst) peek();
                orderByAst.setFields(fieldList);
                return true;
            }
        };
    }

    public Action putOrderGroupByFieldListToGroupByAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                OrderGroupByColumnListAst fieldList = (OrderGroupByColumnListAst) pop();
                GroupByAst groupByAst = (GroupByAst) peek();
                groupByAst.setFields(fieldList);
                return true;
            }
        };
    }

    public Action putAsc_Desc_To_OrderGroupByFieldAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                OrderGroupByFieldAst fieldAst = (OrderGroupByFieldAst) peek();
                fieldAst.setOrder(lastMatch());
                return true;
            }
        };
    }

    public Action pushUpdateSetPairAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast expression = (Ast) pop();
                String fieldName = popStringObjFromTop("updateSetPair_FieldName");
                push(new UpdateSetPairAst(fieldName, expression));
                return true;
            }
        };
    }

    public Action pushUpdateListOfFieldAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                push(new UpdateListOfFieldAst());
                return true;
            }
        };
    }

    public Action putFieldPairInUpdateAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                UpdateSetPairAst fieldPair = (UpdateSetPairAst) pop();
                UpdateListOfFieldAst list = (UpdateListOfFieldAst) peek();
                list.add(fieldPair);
                return true;
            }
        };
    }

    public Action putListOfFieldsIntoTheUpdateAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                UpdateListOfFieldAst fields = (UpdateListOfFieldAst) pop();
                UpdateAst updateAst = (UpdateAst) peek();
                updateAst.setListOfFields(fields);
                return true;
            }
        };
    }

    public Action putFromExpressionToUpdateAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                FromAst fromAst = (FromAst) pop();
                UpdateAst updateAst = (UpdateAst) peek();

                updateAst.setFrom(fromAst);
                return true;
            }
        };
    }

    public Action putWhereExpressionToUpdateAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                WhereAst whereAst = (WhereAst) pop();
                UpdateAst updateAst = (UpdateAst) peek();
                updateAst.setWhereAst(whereAst);
                return true;
            }
        };
    }

    public Action pushUpdateAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                UpdateAst updateAst = new UpdateAst();
                updateAst.setTableName(lastMatch());
                push(updateAst);
                return true;
            }
        };
    }

    public Action pushInsertAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                InsertAst insertAst = new InsertAst();
                insertAst.setTableName(lastMatch());
                push(insertAst);
                return true;
            }
        };
    }

    public Action pushInsertColumnListAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                InsertColumnListAst columnListAst = new InsertColumnListAst();
                push(columnListAst);
                return true;
            }
        };
    }

    public Action pushColumnToInsertColumnListAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                InsertColumnListAst columnListAst = (InsertColumnListAst) peek();
                columnListAst.add(lastMatch());
                return true;
            }
        };
    }

    public Action setColumnListToInsertAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                InsertColumnListAst columnList = (InsertColumnListAst) pop();
                InsertAst insertAst = (InsertAst) peek();
                insertAst.setColumnList(columnList);
                return true;
            }
        };
    }

    public Action setSelectAstToInsertAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                SelectAst selectAst = (SelectAst) pop();
                InsertAst insertAst = (InsertAst) peek();
                insertAst.setAsSelect(selectAst);
                return true;
            }
        };
    }

    public Action setSelectAstToCreateTableAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                SelectAst selectAst = (SelectAst) pop();
                CreateTableAst createAst = (CreateTableAst) peek();
                createAst.setAsSelect(selectAst);
                return true;
            }
        };
    }

    public Action pushBetweenAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                NumberAst rightAst = (NumberAst) pop();
                NumberAst leftAst = (NumberAst) pop();
                Ast valueToCheck = (Ast) pop();

                push(new BetweenAst(valueToCheck, leftAst, rightAst));
                return true;
            }
        };
    }

    public Action createSectionAndPushLastMatch(final String label, final String key) {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                SectionObj section = new SectionObj(label);
                push(section);

                String match = context.getMatch().trim();
                section.pushValue(key, match);
                return true;
            }
        };
    }

    public Action pushValueOnTopSection(final String label, final String key, final Object value) {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                SectionObj section = (SectionObj) peek();
                if (section instanceof SectionObj) {
                    if (!section.getSectionName().equals(label)) {
                        throw new RuntimeException("Expected section '" + label + "' on the top of the stack, but found section '" + section.getSectionName() + "'");
                    }
                }

                section.pushValue(key, value);
                return true;
            }
        };
    }

    public Action pushTopStackValueOnTopSection(final String label, final String key) {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Object value = pop();
                SectionObj section = (SectionObj) peek();
                if (section instanceof SectionObj) {
                    if (!section.getSectionName().equals(label)) {
                        throw new RuntimeException("Expected section '" + label + "' on the top of the stack, but found section '" + section.getSectionName() + "'");
                    }
                }

                section.pushValue(key, value);
                return true;
            }
        };
    }

    public Action pushLastMatchOnTopSection(final String label, final String key) {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                String value = lastMatch();
                SectionObj section = (SectionObj) peek();
                if (section instanceof SectionObj) {
                    if (!section.getSectionName().equals(label)) {
                        throw new RuntimeException("Expected section '" + label + "' on the top of the stack, but found section '" + section.getSectionName() + "'");
                    }
                }

                section.pushValue(key, value);
                return true;
            }
        };
    }

    public Action pushColumnNameAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                SectionObj section = popSectionFromTop("columnSection");
                String tableName = null;
                if (section.contains("columnTable")) {
                    tableName = section.getString("columnTable");//it is for cases where field is specified like here "tableA.column1"
                }
                SimpleFieldAst field = new SimpleFieldAst(tableName, section.getString("columnName"));

                push(field);
                return true;
            }
        };
    }

    public Action pushFieldListAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                push(new FieldListAST());
                return true;
            }
        };
    }

    public Action putFieldAstToFieldList() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast column = (Ast) pop();
                FieldListAST fieldList = (FieldListAST) peek();
                fieldList.addField(column);
                return true;
            }
        };
    }

    public Action pushColumnAsteriskAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                push(new FieldAsteriskAst());
                return true;
            }
        };
    }

    private String lastMatch() {
        return getContext().getMatch().trim();
    }

    public Action pushFromSubqueryAliasAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                String alias = lastMatch();
                Ast tableName = (Ast) pop();
                push(new AliasWrapperAst(tableName, alias, true));
                return true;
            }
        };
    }

    public Action pushTableAliasAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                String alias = lastMatch();
                TableNameAst tableName = (TableNameAst) pop();
                push(new AliasWrapperAst(tableName, alias, true));
                return true;
            }
        };
    }

    public Action wrapLastTableAstInAliasAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                String alias = lastMatch();
                Ast ast = (Ast) pop();
                push(new AliasWrapperAst(ast, alias, true));
                return true;
            }
        };
    }

    public Action pushCaseAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                push(new CaseAst());
                return true;
            }
        };
    }

    public Action pushJoinAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                String joinType = lastMatch();
                push(new JoinAst(joinType));
                return true;
            }
        };
    }

    public Action putFromToSelect() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                FromAst fromExpression = (FromAst) pop();
                SelectAst selectAst = (SelectAst) peek();

                selectAst.setFrom(fromExpression);
                return true;
            }
        };
    }

    public Action pushWhereAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast checkExpression = (Ast) pop();
                WhereAst whereExpression = new WhereAst(checkExpression);
                push(whereExpression);
                return true;
            }
        };
    }

    public Action putWhereToSelect() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                WhereAst whereExpression = (WhereAst) pop();
                SelectAst selectAst = (SelectAst) peek();

                selectAst.setWhere(whereExpression);
                return true;
            }
        };
    }

    public Action putGroupByToSelect() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                GroupByAst groupByExpression = (GroupByAst) pop();
                SelectAst selectAst = (SelectAst) peek();

                selectAst.setGroupBy(groupByExpression);
                return true;
            }
        };
    }

    public Action pushOrderBy() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                OrderByAst orderByExpression = new OrderByAst();
                push(orderByExpression);
                return true;
            }
        };
    }

    public Action pushGroupBy() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                GroupByAst groupByExpression = new GroupByAst();
                push(groupByExpression);
                return true;
            }
        };
    }

    public Action putOrderByToSelect() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                OrderByAst orderByExpression = (OrderByAst) pop();
                SelectAst selectAst = (SelectAst) peek();
                selectAst.setOrderBy(orderByExpression);
                return true;
            }
        };
    }

    public Action setSecondSelectToUnionAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                SelectAst selectAst = (SelectAst) pop();
                UnionAst unionAst = (UnionAst) peek();

                unionAst.setRight(selectAst);
                return true;
            }
        };
    }

    public Action pushUnionAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                SelectAst selectAst = (SelectAst) pop();
                UnionAst union = new UnionAst(selectAst);
                push(union);
                return true;
            }
        };
    }

    public Action setUnionAsAll() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                UnionAst unionAst = (UnionAst) peek();
                unionAst.setUnionAll(true);
                return true;
            }
        };
    }

    public Action putTableToJoin() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast tableExpression = (Ast) pop();
                JoinAst join = (JoinAst) peek();

                join.setTable(tableExpression);
                return true;
            }
        };
    }

    public Action putCheckExpressionOnJoin() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast checkExpression = (Ast) pop();
                JoinAst join = (JoinAst) peek();
                join.setCheckExpression(checkExpression);
                return true;
            }
        };
    }

    public Action addJoinToSelectAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                JoinAst join = (JoinAst) pop();
                SelectAst selectAst = (SelectAst) peek();
                selectAst.addJoin(join);
                return true;
            }
        };
    }

    public Action pushFromAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                push(new FromAst());
                return true;
            }
        };
    }

    public Action addSingleFromRuleToFromAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast ast = (Ast) pop();
                FromAst from = (FromAst) peek();
                from.addRule(ast);
                return true;
            }
        };
    }

    public Action pushNullAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                push(new NullAst());
                return true;
            }
        };
    }

    public Action pushCaseRuleAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast thenRule = (Ast) pop();
                Ast conditionRule = (Ast) pop();
                CaseAst caseAst = (CaseAst) peek();
                caseAst.addCaseRule(new CaseRuleAst(conditionRule, thenRule));
                return true;
            }
        };
    }

    public Action pushCaseElseRuleAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast elseRule = (Ast) pop();
                CaseAst caseAst = (CaseAst) peek();
                caseAst.setElseRule(elseRule);
                return true;
            }
        };
    }

    public Action pushCommitAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                push(new CommitAst());
                return true;
            }
        };
    }

    public Action pushCreateTableAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                push(new CreateTableAst());
                return true;
            }
        };
    }

    public Action setOnCommitCreateRowsInCreateTableAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                CreateTableAst createTable = (CreateTableAst) peek();
                createTable.setOnCommitPreserveRows(true);
                return true;
            }
        };
    }

    public Action setLikeTableToCreateTableAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                TableNameAst tableName = (TableNameAst) pop();
                CreateTableAst createTable = (CreateTableAst) peek();
                createTable.setLikeTableName(tableName);
                return true;
            }
        };
    }

    public Action setIncludingProjectionsToCreateTableAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                CreateTableAst createTable = (CreateTableAst) peek();
                createTable.setIncludingProjections(true);
                return true;
            }
        };
    }

    public Action setLocalTempToCreateTableAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                CreateTableAst createTable = (CreateTableAst) peek();
                createTable.setLocalTemp(true);
                return true;
            }
        };
    }

    public Action setTableNameToCreateTableAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                TableNameAst tableName = (TableNameAst) pop();
                CreateTableAst createTable = (CreateTableAst) peek();
                createTable.setTableName(tableName);
                return true;
            }
        };
    }

    public Action pushSelectAst(final boolean innerQuery) {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                SelectAst selectAst=new SelectAst();
                selectAst.setIsSubquery(innerQuery);
                push(selectAst);
                return true;
            }
        };
    }

    public Action setDropTableIfExists() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                DropTableAST dropTable = (DropTableAST) peek();
                dropTable.setIfExists(true);
                return true;
            }
        };
    }

    public Action setDropTableCascade() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                DropTableAST dropTable = (DropTableAST) peek();
                dropTable.setCascade(true);
                return true;
            }
        };
    }

    public Action setDropTableName() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                DropTableAST dropTable = (DropTableAST) peek();
                dropTable.setTableName(lastMatch());
                return true;
            }
        };
    }

    public Action setSelectDistinct() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                SelectAst selectAst = (SelectAst) peek();
                selectAst.setDistinct(true);
                return true;
            }
        };
    }

    public Action setFieldListToSelectAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                FieldListAST fieldList = (FieldListAST) pop();
                SelectAst selectAst = (SelectAst) peek();
                selectAst.setFieldList(fieldList);
                return true;
            }
        };
    }

    public Action pushStringAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                String match = context.getMatch().trim();
                if (match.startsWith("'")) {
                    match = match.substring(1, match.length() - 1);
                }

                push(new StringAst(match));
                return true;
            }
        };
    }

    public Action pushNumberAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                String match = context.getMatch().trim();
                push(new NumberAst(match));
                return true;
            }
        };
    }

    public Action pushBooleanAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                String match = context.getMatch().trim();
                push(new BooleanAst(match));
                return true;
            }
        };
    }

    public Action pushColumnAliasAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                String aliasName = popHelperString("aliasName");
                Ast ast = (Ast) pop();
                push(new AliasWrapperAst(ast, aliasName, false));
                return true;
            }
        };
    }

    public Action pushCastFunction() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                SqlType type = (SqlType) pop();
                Ast checkExpression = (Ast) pop();
                CastFunctionAst castFunction = new CastFunctionAst(checkExpression, type);
                push(castFunction);
                return true;
            }
        };
    }

    public Action pushConcatenateAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast right = (Ast) pop();
                Ast left = (Ast) pop();
                push(new ConcatenateAst(left, right));
                return true;
            }
        };
    }

    public Action pushParenthesisAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast checkExpression = (Ast) pop();
                push(new ParenthesisAst(checkExpression));
                return true;
            }
        };
    }

    public Action pushFunctionAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                FunctionAst function = new FunctionAst(lastMatch());
                push(function);
                return true;
            }
        };
    }

    public Action setFunctionArgumentAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast argument = (Ast) pop();
                FunctionAst function = (FunctionAst) peek();
                function.addArgument(argument);
                return true;
            }
        };
    }

    public Action pushOverPartitionByAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                OverPartitionByAst ast = new OverPartitionByAst();
                push(ast);
                return true;
            }
        };
    }

    public Action setFieldToOverPartitionByAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                OverPartitionByAst ast = (OverPartitionByAst) peek();
                ast.add(lastMatch());
                return true;
            }
        };
    }

    public Action setOrderByToOverPartitionByAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                OrderByAst orderBy = (OrderByAst) pop();
                OverPartitionByAst ast = (OverPartitionByAst) peek();
                ast.setOrderBy(orderBy);
                return true;
            }
        };
    }

    public Action setOverPartitionByToFunctionAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                OverPartitionByAst overPartitionByAst = (OverPartitionByAst) pop();
                FunctionAst function = (FunctionAst) peek();
                function.setPartitionBy(overPartitionByAst);
                return true;
            }
        };
    }

    public Action pushNotAst() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                Ast checkExpression = (Ast) pop();
                push(new NotAst(checkExpression));
                return true;
            }
        };
    }

    public Action pushSqlType() {
        return new SqlAction() {
            @Override
            public boolean runAction(Context context) {
                SectionObj section = popSectionFromTop(TYPE_CONVERSION);
                String type = (String) section.getValue("type");
                SqlType resultType;
                switch (type.toLowerCase()) {
                    case "varchar":
                        NumberAst size = (NumberAst) section.getValue("size");
                        resultType = new SqlType.VarcharType(Integer.parseInt(size.getValue()));
                        break;
                    case "varbinary":
                        resultType = new SqlType.VarbinaryType();
                        break;
                    case "int":
                        resultType = new SqlType.IntType();
                        break;
                    case "numeric":
                        NumberAst size1Ast = (NumberAst) section.getValue("size1");
                        Integer size1 = Integer.parseInt(size1Ast.getValue());
                        Integer size2 = -1;
                        if (section.contains("size2")) {
                            NumberAst size2Ast = (NumberAst) section.getValue("size2");
                            size2 = Integer.parseInt(size2Ast.getValue());
                        }

                        resultType = new SqlType.NumericType(size1, size2);
                        break;
                    default:
                        throw new RuntimeException("SqlType [" + type + "] is not implemented in parser");
                }
                push(resultType);
                return true;
            }
        };
    }

    /**
     Method tries to get the object from the top of the stack, and if it is not section, or the section does not equal to @sectionName, it throws an exception
     */
    private SectionObj popSectionFromTop(String sectionName) {
        Object val = pop();
        if (val == null || !(val instanceof SectionObj)) {
            throw new RuntimeException("Expected section on the top of the stack, but found [" + val + "]");
        }
        SectionObj section = (SectionObj) val;
        if (!sectionName.equals(section.getSectionName())) {
            throw new RuntimeException("Expected section with name [" + sectionName + "], but found [" + section.getSectionName() + "]");
        }
        return section;
    }

    /**
     Method tries to get the object from the top of the stack, and if it is not stringObj, or the stringObj does not equal to @stringObjName, it throws an exception
     */
    private String popStringObjFromTop(String stringObjName) {
        Object val = pop();
        if (val == null || !(val instanceof StringObj)) {
            throw new RuntimeException("Expected StringObj on the top of the stack, but found [" + val + "]");
        }
        StringObj stringObj = (StringObj) val;
        if (!stringObjName.equals(stringObj.getLabel())) {
            throw new RuntimeException("Expected StringObj with name [" + stringObjName + "], but found [" + stringObj.getLabel() + "]");
        }
        return stringObj.getVal();
    }
}
