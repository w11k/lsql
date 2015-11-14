package com.w11k.lsql;

import java.sql.Connection;
import java.util.concurrent.Callable;

public class TransactionUtils {

    /**
     * Wraps a Callable in a transaction.
     *
     * @param connectionFactory returns the connection used for the transaction control
     * @param from the Callable to wrap
     * @return the wrapped Callable
     */
    public static <T> Callable<T> makeTransactional(final Callable<Connection> connectionFactory, final Callable<T> from) {
        return new Callable<T>() {
            @Override public T call() throws Exception {
                Connection connection = connectionFactory.call();
                try {
                    T result = from.call();
                    connection.commit();
                    return result;
                } catch (Exception e) {
                    connection.rollback();
                    throw e;
                } finally {
                    connection.close();
                }
            }
        };
    }

    /**
     * Wraps a Runnable in a transaction.
     *
     * @param connectionFactory returns the connection used for the transaction control
     * @param from the Runnable to wrap
     * @return the wrapped Runnable
     */
    public static Runnable makeTransactional(final Callable<Connection> connectionFactory, final Runnable from) {
        return new Runnable() {
            @Override public void run() {
                try {
                    makeTransactional(connectionFactory, new Callable<Object>() {
                        @Override public Object call() throws Exception {
                            from.run();
                            return null;
                        }
                    }).call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

}
