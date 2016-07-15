package com.sqlparserproject;

import com.sqlparserproject.ast.Ast;
import com.sqlparserproject.sqlformatter.SqlFormatter;
import java.io.IOException;
import org.apache.commons.io.IOUtils;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParsingResult;

/**
 * @author sad
 */
public class SqlParserMain {

    public static void main(String[] args) throws IOException {
        SqlParsingRules parser = Parboiled.createParser(SqlParsingRules.class);   
        
     //   ParsingResult result2 = new ReportingParseRunner(parser.testStack()).run("11");
        
        
        
        String text = IOUtils.toString(SqlParserMain.class.getResourceAsStream("/com/sqlparserproject/test.txt"));
        text=text.replaceAll("\\?VAR:date_id\\?", "20151216");
        text=text.replaceAll("\\?VAR:task_id\\?", "12");
        text=text.replaceAll("\\?PARAMETER:Topology_Identifiers_HTTP\\?", "'fact_raw_http'");
        text=text.replaceAll("\\??PARAMETER:Topology_Identifiers_FLOW?\\?", "'fact_raw_flow'");
        text=text.replaceAll("\\?PARAMETER:CIMXM_REQUEST_AGG_PV_MASK\\?", "1");
        text=text.replaceAll("\\?PARAMETER:country_mcc\\?", "1");
        text=text.replaceAll("\\?PARAMETER:home_plmn_list\\?", "1,5,6,7,8");
        text=text.replaceAll("\\?PARAMETER:default_apn_value\\?", "aaa");
        text=text.replaceAll("\\?PARAMETER:FLOW.VOL_RETRANS_UP_BUCKET_1.THRESHOLD\\?", "10");
        text=text.replaceAll("\\?PARAMETER:FLOW.VOL_RETRANS_UP_BUCKET_2.THRESHOLD\\?", "10");
        text=text.replaceAll("\\?PARAMETER:FLOW.VOL_RETRANS_UP_BUCKET_3.THRESHOLD\\?", "10");
        text=text.replaceAll("\\?PARAMETER:FLOW.THROUGHPUT_BUCKET_1.THRESHOLD\\?", "10");
        text=text.replaceAll("\\?PARAMETER:FLOW.THROUGHPUT_BUCKET_2.THRESHOLD\\?", "10");
        text=text.replaceAll("\\?PARAMETER:FLOW.THROUGHPUT_BUCKET_3.THRESHOLD\\?", "10");
        text=text.replaceAll("\\?PARAMETER:FLOW.RTT_UP_BUCKET_1.THRESHOLD\\?", "10");
        text=text.replaceAll("\\?PARAMETER:FLOW.RTT_UP_BUCKET_2.THRESHOLD\\?", "10");
        text=text.replaceAll("\\?PARAMETER:FLOW.RTT_UP_BUCKET_3.THRESHOLD\\?", "10");
        text=text.replaceAll("\\?PARAMETER:FLOW.VOL_RETRANS_DOWN_BUCKET_1.THRESHOLD\\?", "10");
        text=text.replaceAll("\\?PARAMETER:FLOW.VOL_RETRANS_DOWN_BUCKET_2.THRESHOLD\\?", "10");
        text=text.replaceAll("\\?PARAMETER:FLOW.VOL_RETRANS_DOWN_BUCKET_3.THRESHOLD\\?", "10");
        text=text.replaceAll("\\?PARAMETER:FLOW.RTT_DOWN_BUCKET_1.THRESHOLD\\?", "10");
        text=text.replaceAll("\\?PARAMETER:FLOW.RTT_DOWN_BUCKET_2.THRESHOLD\\?", "10");
        text=text.replaceAll("\\?PARAMETER:FLOW.RTT_DOWN_BUCKET_3.THRESHOLD\\?", "10");
        text=text.replaceAll("''", "'");
        text=text.replaceAll("&gt;", ">");
        text=text.replaceAll("&lt;", "<");
        ParsingResult result = new ReportingParseRunner(parser.start()).run(text);
        if (result.hasErrors()) {
            throw new RuntimeException("Error ");
        }
        Ast resultAst=(Ast) result.resultValue;
        System.out.println("result=" + resultAst);
        SqlFormatter formatter=new SqlFormatter();
        String recreatedSql=formatter.formatSql(resultAst);
        System.out.println("Result:");
        System.out.println(recreatedSql);
    }
}
