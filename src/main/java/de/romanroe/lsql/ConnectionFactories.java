package de.romanroe.lsql;

import java.sql.Connection;
import java.util.concurrent.Callable;

public final class ConnectionFactories {

    public static Callable<Connection> fromInstance(final Connection connectionInstance) {
        return new Callable<Connection>() {
            @Override
            public Connection call() throws Exception {
                return connectionInstance;
            }
        };
    }

}
