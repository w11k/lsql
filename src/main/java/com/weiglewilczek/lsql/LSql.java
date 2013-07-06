package com.weiglewilczek.lsql;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.Callable;

public class LSql {

    private Callable<Connection> connectionFactory;

    public Callable<Connection> getConnectionFactory() {
        return connectionFactory;
    }

    public void setConnectionFactory(Callable<Connection> connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Connection getConnection() {
        try {
            return connectionFactory.call();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Statement createStatement() {
        try {
            return getConnection().createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeSql(String sql) {
        Connection c = getConnection();
        Statement st;
        try {
            st = c.createStatement();
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public SelectStatement select() {
        return new SelectStatement(this);
    }

    public SelectStatement select(String columns) {
        return new SelectStatement(this, columns);
    }

}
