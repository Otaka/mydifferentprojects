package com.sqlparserproject;

import com.sqlparserproject.ast.*;
import com.sqlparserproject.sqlformatter.SqlFormatter;
import java.util.List;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;
import static org.junit.Assert.*;

public class SqlParserActionsTest {

    public SqlParserActionsTest() {
    }

    private SqlParsingRules createParser() {
        SqlParsingRules parser = Parboiled.createParser(SqlParsingRules.class);
        return parser;
    }

    private Object parseText(String text, Rule rule) {
        SqlParserActions.testDisableActions = false;
        ReportingParseRunner runner = new ReportingParseRunner(rule);
        ParsingResult result = runner.run(text);
        return result.resultValue;
    }

    @Test
    public void testDropTable() {
        DropTableAST dropTableAst = (DropTableAST) parseText("Drop table mytable", createParser().testSqlExpressionLine());
        assertFalse(dropTableAst.isCascade());
        assertFalse(dropTableAst.isIfExists());
        assertEquals("mytable", dropTableAst.getTableName());

        dropTableAst = (DropTableAST) parseText("Drop taBle if exists tablename CASCADE", createParser().testSqlExpressionLine());
        assertTrue(dropTableAst.isCascade());
        assertTrue(dropTableAst.isIfExists());
        assertEquals("tablename", dropTableAst.getTableName());
    }

    @Test
    public void testSelect() {
        SelectAst selectAst = (SelectAst) parseText("Select '34' from mytable", createParser().testSqlExpressionLine());
        assertEquals("34", ((StringAst) selectAst.getFieldList().getField(0)).getValue());

        selectAst = (SelectAst) parseText("Select t.* from mytable t", createParser().testSqlExpressionLine());
        assertEquals("t", ((AliasWrapperAst) selectAst.getFrom().getRules().get(0)).getAlias());
        assertEquals("mytable", ((TableNameAst) (((AliasWrapperAst) selectAst.getFrom().getRules().get(0)).getInternal())).getTableName());

        assertEquals(1, selectAst.getFieldList().getFields().size());
        SimpleFieldAst field = (SimpleFieldAst) selectAst.getFieldList().getFields().get(0);
        assertEquals("*", field.getName());
        assertEquals("t", field.getTable());

        selectAst = (SelectAst) parseText("Select * from mytable", createParser().testSqlExpressionLine());
        assertEquals("mytable", ((TableNameAst) selectAst.getFrom().getRules().get(0)).getTableName());
        assertEquals(FieldAsteriskAst.class, selectAst.getFieldList().getFields().get(0).getClass());
    }

    @Test
    public void testSelectWithFunction() {
        SelectAst selectAst = (SelectAst) parseText("Select sum(field) from mytable", createParser().testSqlExpressionLine());
        FunctionAst function = (FunctionAst) selectAst.getFieldList().getFields().get(0);
        assertEquals("sum", function.getFunctionName());
        assertEquals("field", ((SimpleFieldAst) function.getArguments().get(0)).getName());

        selectAst = (SelectAst) parseText("Select sum(field,field2) from mytable", createParser().testSqlExpressionLine());
        function = (FunctionAst) selectAst.getFieldList().getFields().get(0);
        assertEquals("sum", function.getFunctionName());
        assertEquals("field", ((SimpleFieldAst) function.getArguments().get(0)).getName());
        assertEquals("field2", ((SimpleFieldAst) function.getArguments().get(1)).getName());
    }

    @Test
    public void testSelectWhere() {
        SelectAst selectAst = (SelectAst) parseText("Select * from a where a=3", createParser().testSqlExpressionLine());
        WhereAst where = selectAst.getWhere();
        BinaryOperation op = (BinaryOperation) where.getCheckExpression();
        assertEquals("=", op.getOperation());
        assertEquals("a", ((SimpleFieldAst) op.getLeft()).getName());
        assertEquals("3", ((NumberAst) op.getRight()).getValue());
    }

    @Test
    public void testSelectWhereIn() {
        SelectAst selectAst = (SelectAst) parseText("Select * from a where myfield in (1,2,3,4,5)", createParser().testSqlExpressionLine());
        WhereAst where = selectAst.getWhere();
        InFromValueListAst valList = (InFromValueListAst) where.getCheckExpression();
        assertEquals("1", valList.getValueList().getVariants().get(0));
        assertEquals("5", valList.getValueList().getVariants().get(4));
        assertEquals("myfield", ((SimpleFieldAst) valList.getField()).getName());
    }

    @Test
    public void testBetween() {
        SelectAst selectAst = (SelectAst) parseText("select * from a where myfield BETWEEN 2 AND 6", createParser().testSqlExpressionLine());
        BetweenAst between = (BetweenAst) selectAst.getWhere().getCheckExpression();
        assertEquals("2", between.getLeftBoundary().getValue());
        assertEquals("6", between.getRightBoundary().getValue());
        assertEquals("myfield", ((SimpleFieldAst) between.getValue()).getName());
        String resultSql = new SqlFormatter().formatSql(selectAst);
        assertEquals("SELECT * FROM a WHERE myfield BETWEEN 2 AND 6", resultSql);
    }

    @Test
    public void testSelectWhereNotIn() {
        SelectAst selectAst = (SelectAst) parseText("select fi from my where trim(lower(host)) not in ('classicrock','about.com')", createParser().testSqlExpressionLine());
        WhereAst where = selectAst.getWhere();
        InFromValueListAst valueList = (InFromValueListAst) where.getCheckExpression();
        FunctionAst field = (FunctionAst) valueList.getField();
        assertEquals("trim", field.getFunctionName());
        assertEquals("'classicrock'", valueList.getValueList().getVariants().get(0));
        assertEquals("'about.com'", valueList.getValueList().getVariants().get(1));
        assertTrue(valueList.isNot());
    }

    @Test
    public void testSelectWhereInSelect() {
        SelectAst selectAst = (SelectAst) parseText("Select * from a where myfield in (select v from mytable)", createParser().testSqlExpressionLine());
        WhereAst where = selectAst.getWhere();
        InFromSelectAst selectIn = (InFromSelectAst) where.getCheckExpression();
        SelectAst innerSelect = selectIn.getSelect();
        assertEquals("v", ((SimpleFieldAst) innerSelect.getFieldList().getFields().get(0)).getName());
    }

    @Test
    public void testSelectWithCast() {
        SelectAst selectAst = (SelectAst) parseText("Select myfield::int from a ", createParser().testSqlExpressionLine());
        TypeConversionAst cast = (TypeConversionAst) selectAst.getFieldList().getFields().get(0);

    }

    @Test
    public void testSelectWithJoins() {
        SelectAst selectAst = (SelectAst) parseText("Select * from a LEFT \n OUTER    JOIN b myalias on b.id=a.id2", createParser().testSqlExpressionLine());
        assertEquals(1, selectAst.getJoins().size());
        JoinAst join = selectAst.getJoins().get(0);
        assertEquals("LEFT OUTER JOIN", join.getJoinType());
        AliasWrapperAst alias = (AliasWrapperAst) join.getTable();
        assertEquals("myalias", alias.getAlias());
        assertEquals("b", ((TableNameAst) alias.getInternal()).getTableName());
        BinaryOperation checkExpression = (BinaryOperation) join.getCheckExpression();
        assertEquals("id", ((SimpleFieldAst) checkExpression.getLeft()).getName());
        assertEquals("b", ((SimpleFieldAst) checkExpression.getLeft()).getTable());
        assertEquals("id2", ((SimpleFieldAst) checkExpression.getRight()).getName());
        assertEquals("a", ((SimpleFieldAst) checkExpression.getRight()).getTable());

        selectAst = (SelectAst) parseText("Select * from a LEFT OUTER JOIN (select a::varbinary from table1) myalias2 on b.id=a.id2", createParser().testSqlExpressionLine());
        assertEquals(1, selectAst.getJoins().size());
        join = selectAst.getJoins().get(0);
        alias = (AliasWrapperAst) join.getTable();
        assertEquals("myalias2", alias.getAlias());
        SelectAst innerSelect = (SelectAst) alias.getInternal();
        TableNameAst tableName = (TableNameAst) innerSelect.getFrom().getRules().get(0);
        assertEquals("table1", tableName.getTableName());
    }

    @Test
    public void testSelectFromSubquery() {
        SelectAst selectAst = (SelectAst) parseText("Select * from (select myfield from mytable)", createParser().testSqlExpressionLine());
        assertEquals(FieldAsteriskAst.class, selectAst.getFieldList().getFields().get(0).getClass());
        SelectAst fromSelect = (SelectAst) selectAst.getFrom().getRules().get(0);
        assertEquals("myfield", ((SimpleFieldAst) fromSelect.getFieldList().getFields().get(0)).getName());
        assertEquals("mytable", ((TableNameAst) fromSelect.getFrom().getRules().get(0)).getTableName());
        String sql=new SqlFormatter().formatSql(selectAst);
        assertEquals("SELECT * FROM (SELECT myfield FROM mytable)", sql);
        
        selectAst = (SelectAst) parseText("Select * from (select myfield from mytable) pr", createParser().testSqlExpressionLine());
        assertEquals(FieldAsteriskAst.class, selectAst.getFieldList().getFields().get(0).getClass());
        assertEquals("pr", ((AliasWrapperAst) selectAst.getFrom().getRules().get(0)).getAlias());
        fromSelect = (SelectAst) ((AliasWrapperAst) selectAst.getFrom().getRules().get(0)).getInternal();
        assertEquals("myfield", ((SimpleFieldAst) fromSelect.getFieldList().getFields().get(0)).getName());
        assertEquals("mytable", ((TableNameAst) fromSelect.getFrom().getRules().get(0)).getTableName());
    }

    @Test
    public void testSelectWithFunctionWithPartitionBy() {
        SelectAst selectAst = (SelectAst) parseText("Select sum(field) OVER (PARTITION BY mcc_code ORDER BY mnc_code) from a", createParser().testSqlExpressionLine());
        FunctionAst function = (FunctionAst) selectAst.getFieldList().getFields().get(0);
        assertEquals("sum", function.getFunctionName());
        assertEquals("field", ((SimpleFieldAst) function.getArguments().get(0)).getName());
        List<String> partFields = function.getPartitionBy().getFields();
        assertEquals("mcc_code", partFields.get(0));
        assertEquals("mnc_code", ((SimpleFieldAst) ((OrderGroupByFieldAst) function.getPartitionBy().getOrderBy().getFields().getFields().get(0)).getField()).getName());

        selectAst = (SelectAst) parseText("Select EXTRACT(EPOCH FROM MAX(rawtr)) from a", createParser().testSqlExpressionLine());
        ExtractEpochFromAst epochAst = (ExtractEpochFromAst) selectAst.getFieldList().getFields().get(0);
        FunctionAst maxFunction = (FunctionAst) epochAst.getInternal();
        assertEquals("MAX", maxFunction.getFunctionName());
        assertEquals("rawtr", ((SimpleFieldAst) maxFunction.getArguments().get(0)).getName());
    }

    @Test
    public void testSelectWithCase() {
        SelectAst selectAst = (SelectAst) parseText("select CASE WHEN true THEN '232'||'343' ELSE false END from b", createParser().testSqlExpressionLine());
        CaseAst caseAst = (CaseAst) selectAst.getFieldList().getField(0);
        CaseRuleAst rule = caseAst.getCases().get(0);
        BooleanAst checkExpression = (BooleanAst) rule.getConditionRule();
        assertTrue(checkExpression.getValue());
        ConcatenateAst concatAst = (ConcatenateAst) rule.getBodyRule();
        assertEquals("232", ((StringAst) concatAst.getLeft()).getValue());
        assertEquals("343", ((StringAst) concatAst.getRight()).getValue());
        assertFalse(((BooleanAst) caseAst.getElseRule()).getValue());
    }

    @Test
    public void testSelectWithArithmetics() {
        SelectAst selectAst = (SelectAst) parseText("Select field+(field2*field1) from mytable", createParser().testSqlExpressionLine());
        BinaryOperation argument = (BinaryOperation) selectAst.getFieldList().getFields().get(0);
        assertEquals("field", ((SimpleFieldAst) argument.getLeft()).getName());
        assertEquals("+", argument.getOperation());

        ParenthesisAst parenthesisAst = (ParenthesisAst) argument.getRight();
        assertEquals("*", ((BinaryOperation) parenthesisAst.getInternal()).getOperation());

        assertEquals("field2", ((SimpleFieldAst) ((BinaryOperation) parenthesisAst.getInternal()).getLeft()).getName());
        assertEquals("field1", ((SimpleFieldAst) ((BinaryOperation) parenthesisAst.getInternal()).getRight()).getName());
    }

    @Test
    public void testSelectWithOrderBy() {
        SelectAst selectAst = (SelectAst) parseText("Select * from mytable order by firstField", createParser().testSqlExpressionLine());
        assertEquals("firstField", ((SimpleFieldAst) selectAst.getOrderBy().getFields().getFields().get(0).getField()).getName());

        selectAst = (SelectAst) parseText("Select * from mytable order by firstField asc, t.secondField desc", createParser().testSqlExpressionLine());
        OrderGroupByColumnListAst fieldList = selectAst.getOrderBy().getFields();
        assertEquals("firstField", ((SimpleFieldAst) fieldList.getFields().get(0).getField()).getName());
        assertEquals("asc", fieldList.getFields().get(0).getOrder());
        assertEquals("secondField", ((SimpleFieldAst) fieldList.getFields().get(1).getField()).getName());
        assertEquals("desc", fieldList.getFields().get(1).getOrder());

        selectAst = (SelectAst) parseText("Select * from mytable order by sum(firstField)", createParser().testSqlExpressionLine());
        fieldList = selectAst.getOrderBy().getFields();
        assertEquals("sum", ((FunctionAst) fieldList.getFields().get(0).getField()).getFunctionName());
        assertEquals("firstField", ((SimpleFieldAst) ((FunctionAst) fieldList.getFields().get(0).getField()).getArguments().get(0)).getName());
    }

    @Test
    public void testSelectWithGroupBy() {
        SelectAst selectAst = (SelectAst) parseText("Select * from mytable group by firstField", createParser().testSqlExpressionLine());
        assertEquals("firstField", ((SimpleFieldAst) selectAst.getGroupBy().getFields().getFields().get(0).getField()).getName());
        selectAst = (SelectAst) parseText("Select * from mytable group by firstField, secondField", createParser().testSqlExpressionLine());
        OrderGroupByColumnListAst fieldList = selectAst.getGroupBy().getFields();
        assertEquals("firstField", ((SimpleFieldAst) fieldList.getFields().get(0).getField()).getName());
        assertEquals("secondField", ((SimpleFieldAst) fieldList.getFields().get(1).getField()).getName());
    }

    @Test
    public void testTypeConversion() {
        SelectAst selectAst = (SelectAst) parseText("Select a::numeric(10,2) from mytable", createParser().testSqlExpressionLine());
        SqlType.NumericType type = (SqlType.NumericType) ((TypeConversionAst) selectAst.getFieldList().getField(0)).getSqlType();
        assertEquals(10, (int) type.getSize1());
        assertEquals(2, (int) type.getSize2());
    }

    @Test
    public void testCastFunction() {
        SelectAst selectAst = (SelectAst) parseText("select CAST(1+2 AS NUMERIC(32,12)) as duration from myt", createParser().testSqlExpressionLine());
        AliasWrapperAst alias = (AliasWrapperAst) selectAst.getFieldList().getField(0);
        assertEquals("duration", alias.getAlias());
        CastFunctionAst castFunction = (CastFunctionAst) alias.getInternal();
        assertEquals("numeric(32,12)", castFunction.getType().toString());
        assertEquals("+", ((BinaryOperation) castFunction.getCheckExpression()).getOperation());

//assertEquals(2, );
    }

    @Test
    public void testWhereValueIn() {
        SelectAst selectAst = (SelectAst) parseText("Select a from b where f(2) in (1,2,3,4)", createParser().testSqlExpressionLine());
        InFromValueListAst inValue = (InFromValueListAst) selectAst.getWhere().getCheckExpression();
        FunctionAst function = (FunctionAst) inValue.getField();
        assertEquals("f", function.getFunctionName());
    }

    @Test
    public void testSelectColumnStar() {
        SelectAst selectAst = (SelectAst) parseText("Select mytable.* from mytable", createParser().testSqlExpressionLine());
        String sql=new SqlFormatter().formatSql(selectAst);
        assertEquals("SELECT mytable.* FROM mytable", sql);
    }
    
    @Test
    public void testSelectWhereFromList() {
        SelectAst selectAst = (SelectAst) parseText("Select * from mytable WHERE process_id in ( 'fact_raw_http','34' ) and a=1", createParser().testSqlExpressionLine());
        assertEquals("mytable", ((TableNameAst) selectAst.getFrom().getRules().get(0)).getTableName());
        BinaryOperation andOperation = (BinaryOperation) selectAst.getWhere().getCheckExpression();
        assertEquals("and", andOperation.getOperation());
        InFromValueListAst valueList = (InFromValueListAst) andOperation.getLeft();
        assertEquals("process_id", ((SimpleFieldAst) valueList.getField()).getName());
        assertEquals(2, valueList.getValueList().getVariants().size());
        assertEquals("'fact_raw_http'", valueList.getValueList().getVariants().get(0));
        assertEquals("'34'", valueList.getValueList().getVariants().get(1));
        assertFalse(selectAst.isDistinct());
        selectAst = (SelectAst) parseText("Select distinct * from m WHERE p in (select a from b )", createParser().testSqlExpressionLine());
        assertEquals("m", ((TableNameAst) selectAst.getFrom().getRules().get(0)).getTableName());
        assertTrue(selectAst.isDistinct());
        InFromSelectAst in = (InFromSelectAst) selectAst.getWhere().getCheckExpression();
        assertEquals("p", in.getField().getName());
        SelectAst nestedSelect = in.getSelect();
        assertEquals("b", ((TableNameAst) nestedSelect.getFrom().getRules().get(0)).getTableName());
        assertEquals("a", ((SimpleFieldAst) nestedSelect.getFieldList().getField(0)).getName());
    }

    @Test
    public void testSelectWhereFromListNestedIn() {
        SelectAst selectAst = (SelectAst) parseText("Select * from m WHERE f in (select a from b where c in (3,4) )", createParser().testSqlExpressionLine());
        assertEquals("m", ((TableNameAst) selectAst.getFrom().getRules().get(0)).getTableName());
    }

    @Test
    public void testInsert() {
        InsertAst insertAst = (InsertAst) parseText("insert into mytable(a,b,c) select * from adasd", createParser().testSqlExpressionLine());
        assertEquals("mytable", insertAst.getTableName());
        assertEquals("a", insertAst.getColumnList().getColumnNames().get(0));
        assertEquals("b", insertAst.getColumnList().getColumnNames().get(1));
        assertEquals("c", insertAst.getColumnList().getColumnNames().get(2));
        SelectAst selectAst = insertAst.getAsSelect();
        assertEquals(FieldAsteriskAst.class, selectAst.getFieldList().getFields().get(0).getClass());
        assertEquals("adasd", ((TableNameAst) selectAst.getFrom().getRules().get(0)).getTableName());
    }

    @Test
    public void testUpdate() {
        UpdateAst updateAst = (UpdateAst) parseText("UPDATE mytable SET myfield=1 where myfield=2", createParser().testSqlExpressionLine());
        assertEquals("mytable", updateAst.getTableName());
        assertEquals("myfield", updateAst.getListOfFields().getFieldPairs().get(0).getFieldName());
        assertEquals("1", ((NumberAst) updateAst.getListOfFields().getFieldPairs().get(0).getExpression()).getValue());
        BinaryOperation operation = (BinaryOperation) updateAst.getWhereAst().getCheckExpression();
        assertEquals("=", operation.getOperation());
        assertEquals("myfield", ((SimpleFieldAst) operation.getLeft()).getName());
        assertEquals("2", ((NumberAst) operation.getRight()).getValue());

        updateAst = (UpdateAst) parseText("UPDATE t SET t =1,r=cb.field from cb where a=56", createParser().testSqlExpressionLine());
        assertEquals("t", updateAst.getTableName());
        assertEquals("cb", ((TableNameAst) updateAst.getFrom().getRules().get(0)).getTableName());
    }

    @Test
    public void testCreateTableLikeTable() {
        CreateTableAst createAst = (CreateTableAst) parseText("CREATE TABLE tmp LIKE fact_daily_request INCLUDING PROJECTIONS;", createParser().testSqlExpressionLine());
        assertFalse(createAst.isOnCommitPreserveRows());
        assertTrue(createAst.isIncludingProjections());
        assertFalse(createAst.isLocalTemp());
        assertEquals("tmp", createAst.getTableName().getTableName());
        assertEquals("fact_daily_request", createAst.getLikeTableName().getTableName());

        String resultSql = new SqlFormatter().formatSql(createAst);
        assertEquals("CREATE TABLE tmp LIKE fact_daily_request INCLUDING PROJECTIONS", resultSql);
        
        createAst = (CreateTableAst) parseText("CREATE LOCAL TEMP TABLE tmp ON COMMIT PRESERVE ROWS AS SELECT b.a from b", createParser().testSqlExpressionLine());
        resultSql = new SqlFormatter().formatSql(createAst);
        assertEquals("CREATE LOCAL TEMP TABLE tmp ON COMMIT PRESERVE ROWS AS SELECT b.a FROM b", resultSql);
    }

    @Test
    public void testCreateTable() {
        CreateTableAst createAst = (CreateTableAst) parseText("CREATE TABLE tmp AS SELECT audit_sid FROM fact_raw_http", createParser().testSqlExpressionLine());
        assertFalse(createAst.isOnCommitPreserveRows());
        assertFalse(createAst.isIncludingProjections());
        assertFalse(createAst.isLocalTemp());
        assertEquals("tmp", createAst.getTableName().getTableName());
        assertNull(createAst.getLikeTableName());
        assertNotNull(createAst.getAsSelect());
        SelectAst select = createAst.getAsSelect();
        assertEquals("audit_sid", ((SimpleFieldAst) select.getFieldList().getFields().get(0)).getName());
        assertEquals("fact_raw_http", ((TableNameAst) select.getFrom().getRules().get(0)).getTableName());

        createAst = (CreateTableAst) parseText("CREATE TABLE tmp ON COMMIT PRESERVE ROWS AS SELECT audit_sid FROM fact_raw_http", createParser().testSqlExpressionLine());
        assertTrue(createAst.isOnCommitPreserveRows());

        createAst = (CreateTableAst) parseText("CREATE LOCAL TEMP TABLE tmp AS SELECT audit_sid FROM fact_raw_http", createParser().testSqlExpressionLine());
        assertTrue(createAst.isLocalTemp());
        createAst = (CreateTableAst) parseText("CREATE TABLE tmp AS SELECT audit_sid FROM fact_raw_http INCLUDING PROJECTIONS", createParser().testSqlExpressionLine());
        assertTrue(createAst.isIncludingProjections());
    }

    @Test
    public void testCommit() {
        Ast commitAst = (Ast) parseText("COMMIT;", createParser().testSqlExpressionLine());
        assertEquals(commitAst.getClass(), CommitAst.class);
        String sql = new SqlFormatter().formatSql(commitAst);
        assertEquals("COMMIT", sql);
    }

    @Test
    public void testFormatter() {
        Ast resultAst = (Ast) parseText("select c||b as conc,a::varchar(12),(select b from c) from myfield f where a is not null and not exists(select e from b) and a='asd' and a not in(1,2,3) or e = TRUE", createParser().testSqlExpressionLine());
        assertEquals(SelectAst.class, resultAst.getClass());
        String sql = new SqlFormatter().formatSql(resultAst);
        assertEquals("SELECT c||b as conc, a::varchar(12), (SELECT b FROM c) FROM myfield f WHERE a is NOT NULL and NOT EXISTS(SELECT e FROM b) and a = 'asd' and a NOT IN (1,2,3) or e = TRUE", sql);

        resultAst = (Ast) parseText("select distinct EXTRACT(EPOCH FROM '12-03-2016'),case when a='d' then '1' else '2' end, * from b left outer join t on c=(a+1) UNION ALL select * from c group by b asc order by a; DROP TABLE IF EXISTS b CASCADE", createParser().start());
        assertEquals(SqlListAst.class, resultAst.getClass());
        assertEquals(2, ((SqlListAst)resultAst).getSqls().size());
        sql = new SqlFormatter().formatSql(resultAst);
        assertEquals("SELECT DISTINCT EXTRACT (EPOCH FROM '12-03-2016'), CASE WHEN a = 'd' THEN '1' ELSE '2' END, * FROM b LEFT OUTER JOIN t ON c = (a + 1)\n"
                + "UNION ALL \n"
                + "SELECT * FROM c GROUP BY b asc ORDER BY a;\n"
                + "\n"
                + "DROP TABLE IF EXISTS b CASCADE;", sql);

        resultAst = (Ast) parseText("insert into t (a,b,c) select a,b,c from myt where a not in(select sum(a) over (partition by f1,f2,f3 order by f1) from b)", createParser().testSqlExpressionLine());
        assertEquals(InsertAst.class, resultAst.getClass());
        sql = new SqlFormatter().formatSql(resultAst);
        assertEquals("INSERT INTO t(a,b,c) SELECT a, b, c FROM myt WHERE a NOT IN (SELECT sum(a) OVER(PARTITION BY f1,f2,f3 ORDER BY f1) FROM b)", sql);

        resultAst = (Ast) parseText("select cast(1 as numeric(1,2)) from b", createParser().testSqlExpressionLine());
        assertEquals(SelectAst.class, resultAst.getClass());
        sql = new SqlFormatter().formatSql(resultAst);
        assertEquals("SELECT CAST(1 AS numeric(1,2)) FROM b", sql);
        resultAst = (Ast) parseText("update tabl SET a=1,b=5 from mytable where a=1", createParser().testSqlExpressionLine());
        assertEquals(UpdateAst.class, resultAst.getClass());
        sql = new SqlFormatter().formatSql(resultAst);
        assertEquals("UPDATE tabl SET a=1,b=5 FROM mytable WHERE a = 1", sql);
    }
}
