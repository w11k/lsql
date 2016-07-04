package com.w11k.lsql.tests;

import com.google.common.collect.ImmutableMap;
import com.w11k.lsql.sqlfile.LSqlFile;
import com.w11k.lsql.statement.SqlStatementToPreparedStatement;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class SqlFileReaderTest extends AbstractLSqlTest {

    @Test
    public void readSqlFileForClass() {
        LSqlFile lSqlFile = lSql.readSqlFile(getClass(), "twoStatements.sql");
        ImmutableMap<String, SqlStatementToPreparedStatement> stmts = lSqlFile.getStatements();
        assertEquals(stmts.size(), 2, "wrong number of SQL statements");
    }

}
