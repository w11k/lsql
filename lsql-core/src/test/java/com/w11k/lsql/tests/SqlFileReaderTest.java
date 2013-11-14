package com.w11k.lsql.tests;

import com.google.common.collect.ImmutableMap;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.QueryException;
import com.w11k.lsql.QueriedRow;
import com.w11k.lsql.Query;
import com.w11k.lsql.Row;
import com.w11k.lsql.Table;
import com.w11k.lsql.sqlfile.LSqlFile;
import com.w11k.lsql.sqlfile.LSqlFileStatement;
import com.w11k.lsql.tests.utils.IntWrapper;
import org.testng.annotations.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class SqlFileReaderTest extends AbstractLSqlTest {

    @Test
    public void readSqlFileForClass() {
        LSqlFile lSqlFile = lSql.readSqlFile(DummyService.class);
        ImmutableMap<String, LSqlFileStatement> stmts = lSqlFile.getStatements();
        assertTrue(stmts.size() == 1, "wrong number of SQL statements");
    }

    @Test
    public void readSqlFile() {
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
        ImmutableMap<String, LSqlFileStatement> stmts = lSqlFile.getStatements();
        assertTrue(stmts.size() > 0, "wrong number of SQL statements in file1.sql");
    }

    @Test
    public void executeSqlStatement() {
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
        lSqlFile.statement("create1").execute();
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("age", 10, "content", "text1")).get();
        table1.insert(Row.fromKeyVals("age", 30, "content", "text2")).get();
        table1.insert(Row.fromKeyVals("age", 60, "content", "text3")).get();
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void exceptionOnWrongStatementName() {
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
        lSqlFile.statement("ERRO");
    }

    @Test
    public void executeSqlStatementWithParameters() {
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
        lSqlFile.statement("create1").execute();
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("age", 1, "content", "text1")).get();
        table1.insert(Row.fromKeyVals("age", 3, "content", "text2")).get();
        table1.insert(Row.fromKeyVals("age", 6, "content", "text3")).get();

        assertEquals(lSql.executeRawQuery("select * from table1").asList().size(), 3);
        LSqlFileStatement deleteYoung = lSqlFile.statement("deleteYoung");
        deleteYoung.execute("age", 2);
        assertEquals(lSql.executeRawQuery("select * from table1").asList().size(), 2);
    }

    @Test
    public void executeQueryWithoutChangingParameters() {
        executeSqlStatement();
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
        LSqlFileStatement qInt = lSqlFile.statement("queryRangeMarkers");
        Query query = qInt.query();
        assertEquals(query.asList().size(), 1);
        String firstRow = query.getFirstRow().get().getString("content");
        assertEquals(firstRow, "text1");
    }

    @Test(expectedExceptions = QueryException.class)
    public void executeQueryWithUnusedParameter() {
        executeSqlStatement();
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
        LSqlFileStatement qInt = lSqlFile.statement("queryRangeMarkers");
        qInt.query("WRONG", 1);
    }

    @Test
    public void useNullValueInQuery() {
        executeSqlStatement();
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
        LSqlFileStatement qInt = lSqlFile.statement("queryRangeMarkers");

        Query query = qInt.query("age", null);
        assertFalse(query.getFirstRow().isPresent());
    }

    @Test
    public void parametersInQueryUseCustomColumnConverter() {
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
        lSqlFile.statement("create2").execute();

        Table t2 = lSql.table("table2");
        t2.column("number").setColumnConverter(new Converter() {
            @Override
            public void setValueInStatement(LSql lSql, PreparedStatement ps, int index, Object val) throws SQLException {
                ps.setInt(index, ((IntWrapper) val).getI());
            }

            @Override
            public Object getValueFromResultSet(LSql lSql, ResultSet rs, int index) throws SQLException {
                return new IntWrapper(rs.getInt(index));
            }
        });
        Row r1 = Row.fromKeyVals("number", new IntWrapper(0));
        t2.insert(r1);
        Row r2 = Row.fromKeyVals("number", new IntWrapper(1));
        t2.insert(r2);

        QueriedRow row = lSqlFile.statement("queryColumnConverter").query().getFirstRow().get();
        assertEquals(row.get("number"), new IntWrapper(0));

        row = lSqlFile.statement("queryColumnConverter").query("table2.number", new IntWrapper(1)).getFirstRow().get();
        assertEquals(row, r1);
    }

    @Test
    public void executeQueryRangeParameters() {
        executeSqlStatement();
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
        LSqlFileStatement markers = lSqlFile.statement("queryRangeMarkers");
        System.out.println(markers.getSqlString());
        Query query = markers.query("age", 40);
        List<QueriedRow> result = query.asList();
        assertEquals(result.size(), 2);
    }

}
