package com.w11k.lsql.tests;

import com.google.common.collect.ImmutableMap;
import com.w11k.lsql.Row;
import com.w11k.lsql.Rows;
import com.w11k.lsql.SqlStatement;
import com.w11k.lsql.Table;
import com.w11k.lsql.sqlfile.LSqlFile;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class SqlFileReaderTest extends AbstractLSqlTest {

    @Test
    public void readSqlFileForClass() {
        LSqlFile lSqlFile = lSql.readSqlFile(getClass(), "twoStatements.sql");
        ImmutableMap<String, SqlStatement> stmts = lSqlFile.getStatements();
        assertEquals(stmts.size(), 2, "wrong number of SQL statements");
    }

    @Test
    public void execute() {
        statement("create table table1 (val int);").execute();
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("val", 1));
        table1.insert(Row.fromKeyVals("val", 2));
        table1.insert(Row.fromKeyVals("val", 3));
        Rows rows = lSql.executeRawQuery("SELECT * FROM table1").rows();
        assertEquals(rows.size(), 3);
    }

    private SqlStatement statement(String sqlString) {
        return new SqlStatement(this.lSql, "testStatement", sqlString);
    }

}
