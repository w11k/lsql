package com.w11k.lsql.tests;

import com.google.common.collect.ImmutableMap;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.sqlfile.LazyPreparedStatement;
import com.w11k.lsql.sqlfile.SqlFile;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class SqlFileReaderTest extends AbstractLSqlTest {

    @Test
    public void readSqlFile() {
        SqlFile sqlFile = lSql.sqlFileRelativeToClass(getClass(), "file1.sql");
        ImmutableMap<String, LazyPreparedStatement> stmts = sqlFile.getStatements();
        assertEquals(stmts.size(), 3, "wrong number of SQL statements in file1.sql");
        assertNotNull(stmts.get("getAll"));
        assertNotNull(stmts.get("getSome"));
        assertNull(stmts.get("WRONG_NAME"));
    }

    @Test
    public void executeSqlStatement() {
        SqlFile sqlFile = lSql.sqlFileRelativeToClass(getClass(), "file1.sql");
        sqlFile.execute("create");
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("age", 1)).get();
        table1.insert(Row.fromKeyVals("age", 2)).get();
        table1.insert(Row.fromKeyVals("age", 3)).get();
    }

    @Test
    public void executeQuery() {
        //executeSqlStatement();
        //SqlFile sqlFile = lSql.sqlFileRelativeToClass(getClass(), "file1.sql");
    }

}
