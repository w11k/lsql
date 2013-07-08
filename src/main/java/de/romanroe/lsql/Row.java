package de.romanroe.lsql;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Row implements Map<String, Object> {

    private static Object NOT_YET_FETCHED_VALUE = new Object() {
      public String toString() {
          return "<lazy>";
      }
    };

    private final LSql lSql;

    private final HashMap<String, Object> cache = Maps.newHashMap();

    private final ResultSet resultSet;

    public Row(LSql lSql, ResultSet resultSet) {
        this.lSql = lSql;
        try {
            this.resultSet = resultSet;
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                String key = lSql.getJavaSqlStringConversions().identifierSqlToJava(resultSet.getMetaData().getColumnLabel(i));
                cache.put(key, NOT_YET_FETCHED_VALUE);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getValue(String key) {
        if (cache.get(key) == NOT_YET_FETCHED_VALUE) {
            try {
                cache.put(key, resultSet.getObject(lSql.getJavaSqlStringConversions().identifierJavaToSql(key)));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return cache.get(key);
    }

    public void fetchAllValues() {
        for (String key : cache.keySet()) {
            get(key);
        }
    }

    // ----------------------------------------------------

    @Override
    public int size() {
        return cache.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return cache.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object get(Object key) {
        return getValue(key.toString());
    }

    @Override
    public Object put(String key, Object value) {
        Object old = get(key);
        try {
            resultSet.updateObject(key, value);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return old;
    }

    @Override
    public Object remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<String> keySet() {
        return cache.keySet();
    }

    @Override
    public Collection<Object> values() {
        fetchAllValues();
        return cache.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        fetchAllValues();
        return cache.entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Row row = (Row) o;
        return cache.equals(row.cache);
    }

    @Override
    public int hashCode() {
        return cache.hashCode() * 7;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("cache", cache).toString();
    }
}
