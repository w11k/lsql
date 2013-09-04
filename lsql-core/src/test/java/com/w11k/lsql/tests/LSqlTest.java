package com.w11k.lsql.tests;

import com.w11k.lsql.LSql;
import com.w11k.lsql.dialects.H2Dialect;
import com.w11k.lsql.exceptions.DatabaseAccessException;
import com.w11k.lsql.jdbc.ConnectionUtils;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import static org.testng.Assert.assertNotNull;

public class LSqlTest extends AbstractLSqlTest {

    @Test(dataProvider = "lSqlProvider")
    public void getConnectionFromConnectionFactory(LSqlProvider provider) throws SQLException {
        provider.init(this);
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

    @Test(dataProvider = "lSqlProvider")
    public void execute(LSqlProvider provider) {
        provider.init(this);
        createTable("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO table1 (name, age) VALUES ('cus1', 20)");
    }

    @Test(dataProvider = "lSqlProvider", expectedExceptions = RuntimeException.class)
    public void executeShouldThrowRuntimeExceptionOnWrongStatement(LSqlProvider provider) {
        provider.init(this);
        lSql.executeRawSql("CREATE TABLE table1 (name TEXT, age INT)");
        lSql.executeRawSql("INSERT INTO tableX (name, age) VALUES ('cus1', 20)");
    }

}
