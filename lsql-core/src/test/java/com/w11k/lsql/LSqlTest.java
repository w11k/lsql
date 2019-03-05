package com.w11k.lsql;

import com.w11k.lsql.LSql;
import com.w11k.lsql.exceptions.DatabaseAccessException;
import com.w11k.lsql.jdbc.ConnectionUtils;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import static org.testng.Assert.assertNotNull;

public class LSqlTest extends AbstractLSqlTest {

    @Test
    public void getConnectionFromConnectionFactory() throws SQLException {
        assertNotNull(ConnectionUtils.getConnection(lSql));
    }

    @Test(expectedExceptions = DatabaseAccessException.class)
    public void getConnectionThrowsDatabaseAccessException() throws SQLException {
        LSql l = new LSql(TestConfig.class, new Callable<Connection>() {
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

}
