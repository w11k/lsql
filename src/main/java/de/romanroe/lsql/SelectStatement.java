package de.romanroe.lsql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;

public class SelectStatement {

    private final LSql lSql;
    private final Table table;
    private final String columns;

    public SelectStatement(LSql lSql, Table table, String columns) {
        this.lSql = lSql;
        this.table = table;
        this.columns = columns;
    }

    public Iterable<Row> where(String whereString) {
        Statement st = lSql.createStatement();
        String select = "select " + columns + " from " + table.getTableName();
        if (whereString != null && !whereString.equals("")) {
            select += " where " + whereString;
        }
        try {
            final ResultSet resultSet = st.executeQuery(select);
            final boolean[] hasNext = new boolean[]{resultSet.next()};
            return new Iterable<Row>() {
                @Override
                public Iterator<Row> iterator() {
                    return new Iterator<Row>() {
                        @Override
                        public boolean hasNext() {

                            return hasNext[0];
                        }

                        @Override
                        public Row next() {
                            Row current;
                            try {
                                current = new Row(resultSet);
                                hasNext[0] = resultSet.next();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                            return current;
                        }

                        @Override
                        public void remove() {
                            try {
                                resultSet.deleteRow();
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    };
                }
            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Iterable<Row> run() {
        return where(null);
    }
}
