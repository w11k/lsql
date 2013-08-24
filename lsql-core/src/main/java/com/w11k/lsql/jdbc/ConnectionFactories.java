package com.w11k.lsql.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.Callable;

public final class ConnectionFactories {

    public static Callable<Connection> fromInstance(final Connection connectionInstance) {
        return new Callable<Connection>() {
            public Connection call() throws Exception {
                return connectionInstance;
            }
        };
    }

    public static Callable<Connection> fromDataSource(final DataSource dataDource) {
        return new Callable<Connection>() {
            public Connection call() throws Exception {
                return dataDource.getConnection();
            }
        };
    }


}
