package com.w11k.lsql.jdbc;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.concurrent.Callable;

public final class ConnectionProviders {

    /**
     * Creates a connection provider that always return the same {@code Connection}
     * instance. Useful for tests.
     *
     * @param connectionInstance The connection instance to use
     * @return the provider
     */
    public static Callable<Connection> fromInstance(final Connection connectionInstance) {
        return new Callable<Connection>() {
            public Connection call() throws Exception {
                return connectionInstance;
            }
        };
    }

    /**
     * Creates a connection provider based on a {@code DataSource}.
     *
     * @param dataSource The DataSource to use
     * @return the provider
     */
    public static Callable<Connection> fromDataSource(final DataSource dataSource) {
        return new Callable<Connection>() {
            @Override
            public Connection call() throws Exception {
                return dataSource.getConnection();
            }
        };
    }
}
