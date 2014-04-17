package com.w11k.lsql.tests;

import com.google.common.collect.ImmutableMap;
import com.w11k.lsql.*;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.QueryException;
import com.w11k.lsql.sqlfile.LSqlFile;
import com.w11k.lsql.tests.utils.IntWrapper;
import org.testng.annotations.Test;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class SqlFileReaderTest extends AbstractLSqlTest {

    @Test
    public void readSqlFileForClass() {
        LSqlFile lSqlFile = lSql.readSqlFile(DummyService.class);
        ImmutableMap<String, SqlStatement> stmts = lSqlFile.getStatements();
        assertTrue(stmts.size() == 1, "wrong number of SQL statements");
    }

    @Test
    public void readSqlFile() {
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
        ImmutableMap<String, SqlStatement> stmts = lSqlFile.getStatements();
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

        assertEquals(lSql.executeRawQuery("SELECT * FROM table1").asList().size(), 3);
        SqlStatement deleteYoung = lSqlFile.statement("deleteYoung");
        deleteYoung.execute("table1.age", 2);
        assertEquals(lSql.executeRawQuery("SELECT * FROM table1").asList().size(), 2);
    }

    @Test
    public void executeQueryWithoutChangingParameters() {
        executeSqlStatement();
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
        SqlStatement qInt = lSqlFile.statement("queryRangeMarkers");
        Query query = qInt.query();
        assertEquals(query.asList().size(), 1);
        String firstRow = query.getFirstRow().get().getString("content");
        assertEquals(firstRow, "text1");
    }

    @Test(expectedExceptions = QueryException.class)
    public void executeQueryWithUnusedParameter() {
        executeSqlStatement();
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
        SqlStatement qInt = lSqlFile.statement("queryRangeMarkers");
        qInt.query("WRONG", 1);
    }

    @Test
    public void useNullValueInQuery() {
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
        lSqlFile.statement("create1").execute();
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("age", null, "content", "text1"));
        SqlStatement qInt = lSqlFile.statement("convertOperatorForNullValues");

        Query query = qInt.query("age", null);
        assertTrue(query.getFirstRow().isPresent());
    }

    @Test
    public void parametersInQueryUseCustomColumnConverter() {
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
        lSqlFile.statement("create2").execute();

        Table t2 = lSql.table("table2");
        t2.column("number").setConverter(new Converter() {
            public void setValue(LSql lSql, PreparedStatement ps, int index,
                                 Object val) throws SQLException {
                ps.setInt(index, ((IntWrapper) val).getI());
            }

            public Object getValue(LSql lSql, ResultSet rs, int index) throws SQLException {
                return new IntWrapper(rs.getInt(index));
            }

            public int getSqlTypeForNullValues() {
                return Types.INTEGER;
            }
        });
        Row r1 = Row.fromKeyVals("number", new IntWrapper(0));
        t2.insert(r1);
        Row r2 = Row.fromKeyVals("number", new IntWrapper(1));
        t2.insert(r2);

        QueriedRow row = lSqlFile.statement("queryColumnConverter").query().getFirstRow().get();
        assertEquals(row.get("number"), new IntWrapper(0));

        row = lSqlFile.statement("queryColumnConverter").query("table2.number", new IntWrapper(1))
                .getFirstRow().get();
        assertEquals(row, r1);
    }

    @Test
    public void useFunctionCallbackAsParameter() {
        executeSqlStatement();
        LSqlFile lSqlFile = lSql.readSqlFileRelativeToClass(getClass(), "file1.sql");
        SqlStatement markers = lSqlFile.statement("queryFunctionCallback");
        Query query = markers.query(
                "table1.age", new QueryParameter() {
            @Override
            public void set(PreparedStatement ps, int index) throws SQLException {
                ps.setInt(index, 10);
            }
        }
        );
        List<QueriedRow> result = query.asList();
        assertEquals(result.size(), 1);
    }

}
