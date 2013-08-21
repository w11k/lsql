package com.w11k.lsql.tests;

import com.w11k.lsql.LSql;
import com.w11k.lsql.jdbc.ConnectionFactories;
import com.w11k.lsql.relational.Blob;
import com.w11k.lsql.relational.Row;
import com.w11k.lsql.relational.Table;
import org.h2.jdbcx.JdbcDataSource;
import org.joda.time.DateTime;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.testng.Assert.assertEquals;

public class ConverterTypeTest extends AbstractLSqlTest {

    enum DB {
        POSTGRES("PostgreSQL");
        //MYSQL("MySQL");

        private final String name;

        DB(String name) {
            this.name = name;
        }
    }

    @BeforeMethod @Override public void beforeTest() throws SQLException {
    }

    private void testType(DB db, String sqlTypeName, Object value) {
        testType(db, sqlTypeName, value, value);
    }

    private void testType(DB db, String sqlTypeName, Object value, Object expected) {
        try {
            JdbcDataSource dataSource = new JdbcDataSource();
            dataSource.setURL("jdbc:h2:mem:testdb;MODE=" + db.name);
            final Connection connection;
            connection = dataSource.getConnection();
            connection.setAutoCommit(true);
            lSql = new LSql(ConnectionFactories.fromInstance(connection));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        lSql.executeRawSql("CREATE TABLE table1 (col " + sqlTypeName + ")");
        Table table1 = lSql.table("table1");
        try {
            table1.insert(Row.fromKeyVals("col", value));
            Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow();
            Object storedValue = row.get("col");
            assertEquals(storedValue.getClass(), expected.getClass());
            assertEquals(storedValue, expected);
        } finally {
            lSql.executeRawSql("DROP TABLE table1");
        }
    }

    @Test public void testBoolean() {
        testType(DB.POSTGRES, "BOOL", true);
        testType(DB.POSTGRES, "BOOL", false);
    }

    @Test public void testInt() {
        testType(DB.POSTGRES, "INT", 5);
    }

    @Test public void testFloat() {
        testType(DB.POSTGRES, "FLOAT", 123f, 123d);
    }

    @Test public void testDouble() {
        testType(DB.POSTGRES, "DOUBLE", 123d);
    }

    @Test public void testText() {
        testType(DB.POSTGRES, "TEXT", "foo");
    }

    @Test public void converterCanHandleClobNullValue() throws SQLException {
        super.beforeTest();
        lSql.executeRawSql("CREATE TABLE table1 (col1 TEXT, col2 TEXT)");
        Table table1 = lSql.table("table1");
        table1.insert(Row.fromKeyVals("col1", "val1"));
        Row row = lSql.executeRawQuery("SELECT * FROM table1").getFirstRow();
        assertEquals(row.get("col1"), "val1");
    }

    @Test public void testChar() {
        testType(DB.POSTGRES, "CHAR", 'a');
    }

    @Test public void testVarChar() {
        testType(DB.POSTGRES, "VARCHAR(5)", "123".toCharArray());
    }

    @Test public void testDate() {
        testType(DB.POSTGRES, "TIMESTAMP", DateTime.now());
    }

    @Test public void testBlob() {
        byte[] data = "123456789".getBytes();
        testType(DB.POSTGRES, "BLOB", new Blob(data));
    }

}
