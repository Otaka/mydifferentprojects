package com.sqlparserproject;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

public class SqlParsingRulesTest {

    public SqlParsingRulesTest() {
    }

    @Before
    public void setUp() {
    }

    private SqlParsingRules createParser() {
        SqlParsingRules parser = Parboiled.createParser(SqlParsingRules.class);
        return parser;
    }

    private boolean parseText(String text, Rule rule) {
        SqlParserActions.testDisableActions = true;
        ReportingParseRunner runner = new ReportingParseRunner(rule);
        ParsingResult result = runner.run(text);
        return result.matched;
    }

    @Test
    public void testSqlRule() {
        assertTrue(parseText("DROP TABLE IF EXISTS tmp_request_daily_au;", createParser().testSqlExpressionLine()));
        assertTrue(parseText("DROP TABLE IF EXISTS tmp_request_daily_au CASCADE;", createParser().testSqlExpressionLine()));
        assertTrue(parseText("CREATE LOCAL TEMP TABLE tmp ON COMMIT PRESERVE ROWS AS SELECT audit_sid FROM fact_raw_http;", createParser().testSqlExpressionLine()));
        assertTrue(parseText("CREATE LOCAL TEMP TABLE tmp ON COMMIT PRESERVE ROWS AS SELECT audit_sid FROM fact_raw_http INCLUDING PROJECTIONS;", createParser().testSqlExpressionLine()));
    }

    @Test
    public void testCreateTableRule() {
        assertTrue(parseText("CREATE TABLE tmp_req LIKE mytable INCLUDING PROJECTIONS", createParser().testCreateTable()));
        assertTrue(parseText("CREATE LOCAL TEMP TABLE tmp_req ON COMMIT PRESERVE ROWS AS SELECT * from mytable", createParser().testCreateTable()));

    }

    @Test
    public void testGroupByRule() {
        assertTrue(parseText("GRoup by a, b.field3", createParser().testGroupByRule()));
        assertTrue(parseText("GRoup by a.field", createParser().testGroupByRule()));
        assertTrue(parseText("GRoup by a, b", createParser().testGroupByRule()));
        try {
            assertFalse(parseText("GRoup with a.field, b.field3", createParser().testGroupByRule()));
            fail("should fail");
        } catch (Exception ex) {
        }
        try {
            assertFalse(parseText("GRoup by .field, b.field3", createParser().testGroupByRule()));
            fail("should fail");
        } catch (Exception ex) {
        }
        try {
            assertFalse(parseText("GRoup by a., b.field3", createParser().testGroupByRule()));
            fail("should fail");
        } catch (Exception ex) {
        }
        try {
            assertFalse(parseText("GRoup by , b.field3", createParser().testGroupByRule()));
            fail("should fail");
        } catch (Exception ex) {
        }
        assertTrue(parseText("GRoup by a, b", createParser().testGroupByRule()));
    }

    @Test
    public void testOrderByRule() {
        assertTrue(parseText("order By a.field", createParser().testOrderBy()));
        assertTrue(parseText("order By a.field asc", createParser().testOrderBy()));
        assertTrue(parseText("order By a.field desc", createParser().testOrderBy()));
        assertTrue(parseText("order By a.field,b desc", createParser().testOrderBy()));
        assertTrue(parseText("order By a.field asc,b desc", createParser().testOrderBy()));
        assertTrue(parseText("order By SUM(fre)", createParser().testOrderBy()));
        assertTrue(parseText("order By SUM(fre),sad", createParser().testOrderBy()));
        try {
            assertFalse(parseText("order By desc", createParser().testOrderBy()));
            fail("should fail");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testFrom() {
        assertTrue(parseText("mytable", createParser().fromExpression()));
        assertTrue(parseText("mytable a", createParser().fromExpression()));
        assertTrue(parseText("mytable a,mytable2 b", createParser().fromExpression()));
        assertTrue(parseText("mytable,mytable2", createParser().fromExpression()));
        try {
            assertFalse(parseText(" ", createParser().fromExpression()));
            fail("should fail");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testSelectRule() {
        assertTrue(parseText("select a from tableB", createParser().testSelectExpression()));
        assertTrue(parseText("select b.b,a.asd,sd from tableB b,tableA a", createParser().testSelectExpression()));
        assertTrue(parseText("select b.*,a.asd,sd from tableB b,tableA a", createParser().testSelectExpression()));
        assertTrue(parseText("select * from (select * from my str)", createParser().testSelectExpression()));
        assertTrue(parseText("select a.* from tablea a", createParser().testSelectExpression()));
        assertTrue(parseText("select * from (select * from my str) asad", createParser().testSelectExpression()));
        assertTrue(parseText("select * from (select * from my str) b,mytable a", createParser().testSelectExpression()));
        assertTrue(parseText("select * from mytable a,(select * from my str) b", createParser().testSelectExpression()));
        assertTrue(parseText("select * from mytable a where field in(1,2,3,4)", createParser().testSelectExpression()));
        try {
            assertFalse(parseText("select from (select * from my str)", createParser().testSelectExpression()));
            fail("Should fail");
        } catch (Exception ex) {
        }
        try {
            assertFalse(parseText("select * from ", createParser().testSelectExpression()));
            fail("Should fail");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testSelectWithCalculationRule() {
        assertTrue(parseText("select a=1 from tableB", createParser().testSelectExpression()));
        assertTrue(parseText("select a=1+1 from tableB", createParser().testSelectExpression()));
    }

    @Test
    public void testSelectWithWhereRule() {
        assertTrue(parseText("select * from b where b.a=4", createParser().testSelectExpression()));
    }

    @Test
    public void testSelectWithCaseRule() {
        assertTrue(parseText("select CASE WHEN true THEN '232'||'343' ELSE false END from b", createParser().testSelectExpression()));
        assertTrue(parseText("select CASE WHEN true THEN '232' ELSE false END from b", createParser().testSelectExpression()));
        assertTrue(parseText("select CASE WHEN true THEN '232' WHEN false THEN '567' ELSE false END from b", createParser().testSelectExpression()));
        assertTrue(parseText("select CASE WHEN d=34 THEN -1 ELSE false END AS v, b.t from b where b.a=4", createParser().testSelectExpression()));
        try {
            assertFalse(parseText("select when case d=34 THEN -1 ELSE false END AS v, b.t from b where b.a=4", createParser().testSelectExpression()));
            fail("should fail");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testSelectWithJoinRule() {
        assertTrue(parseText("select * from a left outer join b on a.id=b.id", createParser().testSelectExpression()));
        assertTrue(parseText("select * from a right outer join b on a.id=b.id", createParser().testSelectExpression()));
        assertTrue(parseText("select * from a right join b on a.id=b.id", createParser().testSelectExpression()));
        assertTrue(parseText("select * from a left join b on a.id=b.id", createParser().testSelectExpression()));
        assertTrue(parseText("select * from a join b on a.id=b.id", createParser().testSelectExpression()));
        assertTrue(parseText("select * from a inner join b on a.id=b.id", createParser().testSelectExpression()));
        assertTrue(parseText("select * from a full join b on a.id=b.id", createParser().testSelectExpression()));
        assertTrue(parseText("select * from a left outer join (select * from b) dcc on a.id=b.id", createParser().testSelectExpression()));

    }

    @Test
    public void testSelectWithManyJoinsRule() {
        assertTrue(parseText("select * from a left outer join b on a.id=b.id left outer join c on a.id=c.id", createParser().testSelectExpression()));
        assertTrue(parseText("select * from a left outer join b on (b.id=34)", createParser().testSelectExpression()));
        assertFalse(parseText("select * from a left outer join b on a.id=b.id let outer join c on a.id=c.id", createParser().testSelectExpression()));
    }

    @Test
    public void testSelectWithPartitionBy() {
        assertTrue(parseText("SELECT a, EXTRACT(EPOCH FROM MAX(raw_transaction_end) OVER (PARTITION BY raw_msisdn, session_id) - MIN(raw_transaction_start) OVER (PARTITION BY raw_msisdn, session_id) ) from tableA", createParser().testSelectExpression()));

        assertTrue(parseText("select b,ROW_NUMBER() OVER (PARTITION BY mcc_code ORDER BY mnc_code) from a", createParser().testSelectExpression()));
        assertTrue(parseText("select b,ROW_NUMBER() OVER (PARTITION BY mcc_code,sd ORDER BY mnc_code) as mc,s from a", createParser().testSelectExpression()));
        assertTrue(parseText("SELECT ROW_NUMBER() OVER( PARTITION BY visit_number ORDER BY rat_sid desc) as visit_index from mytable", createParser().testSelectExpression()));

    }

    @Test
    public void testConcatenationCaseRule() {
        assertTrue(parseText("select b.a||b.b from b where b.a=4", createParser().testSelectExpression()));
        try {
            assertFalse(parseText("select b.a|b.b from b where b.a=4", createParser().testSelectExpression()));
            fail("Should fail");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testSelectWithCastFunction() {
        assertTrue(parseText("select CAST(1*65 AS NUMERIC(23,45)) from b", createParser().testSelectExpression()));
        assertTrue(parseText("select CAST(1.0*SUM(pv_duration)/MAX(visit_duration) AS NUMERIC(32,12)) as duration from b", createParser().testSelectExpression()));
        assertTrue(parseText("select a,CAST(1.0*SUM(pv_duration)/MAX(visit_duration) AS NUMERIC(32,12)) as duration,b from b", createParser().testSelectExpression()));
    }

    @Test
    public void testSelectWithTypeConversion() {
        assertTrue(parseText("select a::int from b", createParser().testSelectExpression()));
        assertTrue(parseText("select max(a::int) from b", createParser().testSelectExpression()));
        assertTrue(parseText("select max(a)::int from b", createParser().testSelectExpression()));
        assertTrue(parseText("select max(a)::numeric(2334343,342234223) from b", createParser().testSelectExpression()));
        assertTrue(parseText("select (34-65+65)::int / (65*98)::numeric(1,2) from b", createParser().testSelectExpression()));
        try {
            assertFalse(parseText("select max(a::int):varchar from b", createParser().testSelectExpression()));
            fail("Should fail");
        } catch (Exception ex) {
        }
        assertTrue(parseText("varchar(34)", createParser().testTypeConversionVariantRule()));
    }

    @Test
    public void testBeetween() {
        assertTrue(parseText("SELECT a,b from b where a between 1 and 5", createParser().testSelectExpression()));
    }

    @Test
    public void testExtractEpoch() {
        assertTrue(parseText("SELECT EXTRACT(EPOCH FROM lead_ts)  from b", createParser().testSelectExpression()));
        assertTrue(parseText("SELECT EXTRACT(EPOCH FROM lead_ts - 45)  from b", createParser().testSelectExpression()));
        assertTrue(parseText("SELECT CASE WHEN EXTRACT(EPOCH FROM lead_ts - ts) >= 1800 THEN 1800 ELSE 2  END  from b", createParser().testSelectExpression()));
    }

    @Test
    public void testSelectWithSubqueryInvocation() {
        assertTrue(parseText("SELECT ab,(select count(1) from mytable) as count,bc from b", createParser().testSelectExpression()));
    }

    @Test
    public void testSelectWithFunctionInvocation() {
        assertTrue(parseText("select count(1,4) from b", createParser().testSelectExpression()));

        assertTrue(parseText("SELECT now() from b", createParser().testSelectExpression()));
        assertTrue(parseText("SELECT SUBSTR(sub_opnet_sid) FROM fact_raw_http f", createParser().testSelectExpression()));
        assertTrue(parseText("select count(1) from b", createParser().testSelectExpression()));

        assertTrue(parseText("select count(*) from b", createParser().testSelectExpression()));
        assertTrue(parseText("select max(b.field_1),min(b.field_2) from b", createParser().testSelectExpression()));
        assertTrue(parseText("select max(b.field_1) as rawApn,min(b.field_2) as sdsda from b", createParser().testSelectExpression()));
        assertTrue(parseText("select max(b.field_1,a) as rawApn,min(b.field_2) as sdsda from b", createParser().testSelectExpression()));
        assertTrue(parseText("select max(a) as rawApn,min(b.field_2) as sdsda from b", createParser().testSelectExpression()));
        try {
            assertFalse(parseText("select count(* from b", createParser().testSelectExpression()));
            fail("Should fail");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testSelectWithComplexFunctionInvocation() {
        assertTrue(parseText("select SUBSTR(fs.raw_msisdn,1,LENGTH(fs.raw_msisdn)-4),count(*) from b", createParser().testSelectExpression()));
    }

    @Test
    public void testStringRule() {
        assertTrue(parseText("'34'", createParser().stringRule()));
    }

    @Test
    public void testValuesListRule() {
        assertTrue(parseText("'23','43','6'", createParser().testValuesListForInExpression()));
        assertTrue(parseText("2,43,6", createParser().testValuesListForInExpression()));
    }

    @Test
    public void testWhereRule() {

        assertFalse(parseText("where a b", createParser().testWhereExpression()));

        assertTrue(parseText("where substr(b)::int=1", createParser().testWhereExpression()));
        assertTrue(parseText("where substr(b)=1", createParser().testWhereExpression()));
        assertTrue(parseText("where b.a=1", createParser().testWhereExpression()));
        assertTrue(parseText("where b.a='34'", createParser().testWhereExpression()));
        assertTrue(parseText("where a=b", createParser().testWhereExpression()));
        assertTrue(parseText("where a=true", createParser().testWhereExpression()));
        assertTrue(parseText("where a=false", createParser().testWhereExpression()));
        assertTrue(parseText("where a in ('23','43','6')", createParser().testWhereExpression()));
        assertTrue(parseText("where a=1", createParser().testWhereExpression()));
        assertTrue(parseText("where b.a=(1+1)*6", createParser().testWhereExpression()));
        assertTrue(parseText("where b.a=c.yty", createParser().testWhereExpression()));
        assertTrue(parseText("where (b.a=c.yty)", createParser().testWhereExpression()));
        assertTrue(parseText("where a in (select t.field from tbl t)", createParser().testWhereExpression()));

        assertFalse(parseText("where a on (select t.field from tbl t)", createParser().testWhereExpression()));

        assertTrue(parseText("where a=5 and b=434", createParser().testWhereExpression()));
        assertTrue(parseText("where a=5 and (b=434 or c='asdasd sadas')", createParser().testWhereExpression()));
        assertTrue(parseText("where a=5 and (b in (select * from mytable))", createParser().testWhereExpression()));
        assertTrue(parseText("where EXISTS (SELECT * FROM tmp_request_daily_audits a WHERE f.audit_sid = a.audit_sid)", createParser().testWhereExpression()));
        assertTrue(parseText("where NOT EXISTS (SELECT * FROM tmp)", createParser().testWhereExpression()));
        try {
            assertFalse(parseText("where EISTS (SELECT * FROM tmp_request_daily_audits a WHERE f.audit_sid = a.audit_sid)", createParser().testWhereExpression()));
            fail("should fail");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testWhereWithCaseRule() {
        assertTrue(parseText("where a=(case when b=1 then 6 else 5 end)", createParser().testWhereExpression()));
        assertTrue(parseText("where a = case when b=1 then 6 else 5 end", createParser().testWhereExpression()));

    }

    @Test
    public void testSelectWithFunctionAndComparingRule() {
        assertTrue(parseText("select max(3)=3 from tabl", createParser().testSelectExpression()));
        assertTrue(parseText("where trim('s') not in ('3','34')", createParser().testWhereExpression()));
        assertTrue(parseText("where a=1 and not trim('s')", createParser().testWhereExpression()));
    }

    @Test
    public void testCaseRule() {
        assertTrue(parseText("CASE WHEN a(3)::int = 1 THEN TRUE ELSE FALSE END", createParser().testCaseRule()));
        assertTrue(parseText("CASE WHEN SUBSTR(a,2,3) = 1 THEN TRUE ELSE FALSE END", createParser().testCaseRule()));
        assertTrue(parseText("CASE WHEN a = 6 THEN TRUE ELSE FALSE END", createParser().testCaseRule()));
        assertTrue(parseText("CASE WHEN a = 6 THEN TRUE END", createParser().testCaseRule()));
        assertTrue(parseText("CASE WHEN a = 6 and b=false THEN TRUE END", createParser().testCaseRule()));
        assertTrue(parseText("CASE WHEN a = 6 THEN true WHEN a=6 THEN false END", createParser().testCaseRule()));
        try {
            assertFalse(parseText("CASE WHEN a = 6 THEN TRUE", createParser().testCaseRule()));
            fail("Should fail");
        } catch (Exception ex) {
        }
        assertFalse(parseText("WHEN  CASE a = 6 THEN TRUE ELSE FALSE END", createParser().testCaseRule()));

    }

    @Test
    public void testUnion() {
        assertTrue(parseText("SELECT distinct * from mytable r where c=34 UNION select*from mytable2", createParser().testSelectExpression()));
        assertTrue(parseText("SELECT * from mytable UNION ALL select*from mytable2", createParser().testSelectExpression()));
        try {
            assertFalse(parseText("SELECT * from mytable UNION ALL2 select*from mytable2", createParser().testSelectExpression()));
            fail("Should fail");
        } catch (Exception ex) {
        }
    }

    @Test
    public void testUpdate() {
        assertTrue(parseText("UPDATE t SET t = 1", createParser().testUpdateRule()));
        assertTrue(parseText("UPDATE t SET t = nextval() where a=56", createParser().testUpdateRule()));
        assertTrue(parseText("UPDATE t SET t =1,r=cb.field from cb where a=56", createParser().testUpdateRule()));
    }

    @Test
    public void testInsertFromSelect() {
        assertTrue(parseText("insert into mytable(a,b,c,d) select * from b", createParser().testInsertRule()));
    }

    @Test
    public void testBigSql() {
        assertTrue(parseText("SELECT probe_sid, \n"
                + "           substr(raw_probe_name) as raw_probe_name\n"
                + "           FROM fact_raw_http f\n"
                + "           WHERE date_id = 12\n"
                + "           GROUP BY probes_id, raw_probename", createParser().testSelectExpression()));

        assertTrue(parseText("SELECT raw_msisdn,\n"
                + "                     MAX(raw_apn) AS raw_apn,\n"
                + "                     MAX(raw_imsi) AS raw_imsi,\n"
                + "                     MAX(sub_opnet_sid) AS sub_opnet_sid \n"
                + "                FROM fact_raw_http f\n"
                + "               WHERE date_id = 4565\n"
                + "                 AND (dm.pattern_type = 'Application' OR pv_mask IN (878787,45)) \n"
                + "                 AND (dm.pattern_sid IS NULL OR dm.media_category_name != 'Promotional Servers') \n"
                + "                 AND EXISTS (SELECT * FROM tmp_request_daily_audits_12 a WHERE f.audit_sid = a.audit_sid)\n"
                + "               GROUP BY raw_msisdn", createParser().testSelectExpression()));

        assertTrue(parseText("SELECT raw_msisdn, MAX(raw_apn) AS raw_apn, MAX(raw_imsi) AS raw_imsi, MAX(sub_opnet_sid) AS sub_opnet_sid FROM fact_raw_http f", createParser().testSelectExpression()));
    }
}
