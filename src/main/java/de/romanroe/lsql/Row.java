package de.romanroe.lsql;

import com.google.common.collect.Maps;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Row implements Map<String, Object> {

    public final ResultSet resultSet;
    public final int rowNumber;

    private final HashMap<String, Object> data = Maps.newHashMap();

    public Row(ResultSet resultSet) {
        try {
            this.resultSet = resultSet;
            this.rowNumber = resultSet.getRow();
            for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
                data.put(resultSet.getMetaData().getColumnLabel(i), resultSet.getObject(i));

            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Object getValue(String key) {
        return data.get(key);
    }

    // ----------------------------------------------------

    @Override
    public int size() {
        return data.size();
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return data.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return data.containsValue(value);
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
        return data.keySet();
    }

    @Override
    public Collection<Object> values() {
        return data.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return data.entrySet();
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
        return data.equals(row.data);
    }

    @Override
    public int hashCode() {
        return data.hashCode() * 7;
    }

    @Override
    public String toString() {
        return "Row{" +
                "no=" + rowNumber + ", " +
                "data=" + data +
                '}';
    }
}
