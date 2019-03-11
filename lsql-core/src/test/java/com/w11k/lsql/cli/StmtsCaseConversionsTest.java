package com.w11k.lsql.cli;

import com.google.common.collect.Maps;
import com.w11k.lsql.AbstractLSqlTest;
import com.w11k.lsql.cli.java.JavaExporter;
import com.w11k.lsql.cli.java.StatementFileExporter;
import com.w11k.lsql.query.PlainQuery;
import com.w11k.lsql.statement.AnnotatedSqlStatementToQuery;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

public final class StmtsCaseConversionsTest extends AbstractLSqlTest {

    @Test
    public void gen() {
        lSql.executeRawSql("create table case_conversions2 (id integer primary key, col1 int)");

        JavaExporter javaExporter = new JavaExporter(this.lSql, null);

        Map<String, AnnotatedSqlStatementToQuery<PlainQuery>> stmts = Maps.newHashMap();
        AnnotatedSqlStatementToQuery<PlainQuery> st = this.lSql.createSqlStatement(
                "select\n" +
                        "id,\n" +
                        "col1 as \"one_two: int\",\n" +
                        "col1 as \"onetwo: int\"\n" +
                        "from case_conversions2;"
        );
        stmts.put("st1", st);

        StatementFileExporter stmtExporter = new StatementFileExporter(
                this.lSql,
                javaExporter,
                "/tmp",
                "/tmp/file.sql",
                "testfile.sql",
                stmts);

        boolean foundOneTwo = stmtExporter.getStmtRowDataClassMetaList().stream()
                .anyMatch(dc ->
                        dc.getPackageName().equals("testfile")
                                && dc.getFields().stream()
                                .anyMatch(field ->
                                        field.getColumnInternalSqlName().equals("one_two")
                                                && field.getColumnJavaCodeName().equals("oneTwo")));
        Assert.assertTrue(foundOneTwo);

        boolean foundOnetwo = stmtExporter.getStmtRowDataClassMetaList().stream()
                .anyMatch(dc ->
                        dc.getPackageName().equals("testfile")
                                && dc.getFields().stream()
                                .anyMatch(field ->
                                        field.getColumnInternalSqlName().equals("onetwo")
                                                && field.getColumnJavaCodeName().equals("onetwo")));
        Assert.assertTrue(foundOnetwo);
    }


}
