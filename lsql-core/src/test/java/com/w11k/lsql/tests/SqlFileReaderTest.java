package com.w11k.lsql.tests;

import com.google.common.collect.ImmutableMap;
import com.w11k.lsql.relational.Query;
import com.w11k.lsql.relational.Row;
import com.w11k.lsql.relational.Table;
import com.w11k.lsql.sqlfile.SqlFile;
import com.w11k.lsql.sqlfile.SqlFileStatement;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class SqlFileReaderTest extends AbstractLSqlTest {

    @Test
    public void readSqlFile() {
        SqlFile sqlFile = lSql.sqlFileRelativeToClass(getClass(), "file1.sql");
        ImmutableMap<String, SqlFileStatement> stmts = sqlFile.getStatements();
        assertEquals(stmts.size(), 4, "wrong number of SQL statements in file1.sql");
    }

    @Test
    public void executeSqlStatement() {
        SqlFile sqlFile = lSql.sqlFileRelativeToClass(getClass(), "file1.sql");
        sqlFile.statement("create").execute();
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("age", 10, "content", "text1")).get();
        table1.insert(Row.fromKeyVals("age", 30, "content", "text2")).get();
        table1.insert(Row.fromKeyVals("age", 60, "content", "text3")).get();
    }

    @Test
    public void executeQueryWithoutChangingParameters() {
        executeSqlStatement();
        SqlFile sqlFile = lSql.sqlFileRelativeToClass(getClass(), "file1.sql");
        SqlFileStatement qInt = sqlFile.statement("queryWithIntegerArg");
        Query query = qInt.query();
        String contentForAge60 = query.getFirstRow().getString("content");
        assertEquals(contentForAge60, "text3", "Row with age==60 has content==text3");
    }

    @Test
    public void executeQueryWithChangedUnquotedParameter() {
        executeSqlStatement();
        SqlFile sqlFile = lSql.sqlFileRelativeToClass(getClass(), "file1.sql");
        SqlFileStatement qInt = sqlFile.statement("queryWithIntegerArg");
        Query query = qInt.query("age", 20);
        assertEquals(query.asList().size(), 2, "query should return 2 rows with age>20");
    }

    @Test
    public void executeQueryWithChangedQuotedParameter() {
        executeSqlStatement();
        SqlFile sqlFile = lSql.sqlFileRelativeToClass(getClass(), "file1.sql");
        SqlFileStatement qInt = sqlFile.statement("queryWithStringArg");

        Query query = qInt.query("content", "text1");
        assertEquals(query.getFirstRow().getInt("age"), 10);

        query = qInt.query("content", "text2");
        assertEquals(query.getFirstRow().getInt("age"), 30);

        query = qInt.query("content", "text3");
        assertEquals(query.getFirstRow().getInt("age"), 60);
    }

}
