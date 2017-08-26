package com.w11k.lsql.tests;

import com.beust.jcommander.internal.Sets;
import com.w11k.lsql.Column;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Table;
import com.w11k.lsql.dialects.H2Dialect;
import com.w11k.lsql.exceptions.DatabaseAccessException;
import com.w11k.lsql.jdbc.ConnectionUtils;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.Callable;

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

public class LSqlTest extends AbstractLSqlTest {

    @Test
    public void getConnectionFromConnectionFactory() throws SQLException {
        assertNotNull(ConnectionUtils.getConnection(lSql));
    }

    @Test(expectedExceptions = DatabaseAccessException.class)
    public void getConnectionThrowsDatabaseAccessException() throws SQLException {
        LSql l = new LSql(new H2Dialect(), new Callable<Connection>() {
            @Override
            public Connection call() throws Exception {
                throw new RuntimeException();
            }
        });
        ConnectionUtils.getConnection(l);
    }

    @Test
    public void execute() {
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
    }

    @Test(expectedExceptions = RuntimeException.class)
    public void executeShouldThrowRuntimeExceptionOnWrongStatement() {
        lSql.executeRawSql("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO tableX (name, age) VALUES ('cus1', 20)");
    }

    @Test
    public void fetchMetaDataForAllTables() throws SQLException {
        createTable("CREATE TABLE table1 (a TEXT, b INT)");
        createTable("CREATE TABLE table2 (c TEXT, d INT)");

        lSql.fetchMetaDataForAllTables();

        Set<String> tableWithColumns = Sets.newHashSet();
        Iterable<Table> tables = lSql.getTables();
        for (Table table : tables) {
            for (Column column : table.getColumns().values()) {
                tableWithColumns.add(table.getTableName() + "-" + column.getJavaColumnName());
            }
        }

        assertTrue(tableWithColumns.contains("table1-a"));
        assertTrue(tableWithColumns.contains("table1-b"));
        assertTrue(tableWithColumns.contains("table2-c"));
        assertTrue(tableWithColumns.contains("table2-d"));
    }

}
