package com.w11k.relda;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

public class Query implements Iterable<LMap> {

    private final LSql lSql;
    private final String sql;

    public Query(LSql lSql, String sql) {
        this.lSql = lSql;
        this.sql = sql;
    }

    @Override public Iterator<LMap> iterator() {
        final boolean[] hasNext = {false};
        try {
            final ResultSet resultSet = lSql.createStatement().executeQuery(sql);
            hasNext[0] = resultSet.next();
            return new Iterator<LMap>() {
                @Override public boolean hasNext() {
                    return hasNext[0];
                }

                @Override public LMap next() {
                    LMap lMap = LMap.fromMap(new ResultSetMap(lSql, resultSet));
                    try {
                        hasNext[0] = resultSet.next();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    return lMap;
                }

                @Override public void remove() {
                    try {
                        resultSet.deleteRow();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void run() {
        iterator();
    }

    public <T> List<T> map(Function<LMap, T> rowHandler) {
        List<T> list = Lists.newLinkedList();
        for (LMap lMap : this) {
            list.add(rowHandler.apply(lMap));
        }
        return list;
    }

    public LMap getFirstRow() {
        return iterator().next();
    }

}
