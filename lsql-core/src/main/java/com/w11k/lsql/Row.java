package com.w11k.lsql;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 *
 */
public class Row extends ForwardingMap<String, Object> {

    private final Map<String, Object> data;

    //private final Map<String, List<Row>> joinedRows;

    public static Row fromKeyVals(Object... keyVals) {
        Row r = new Row();
        r.addKeyVals(keyVals);
        return r;
    }

    public Row() {
        this(Maps.<String, Object>newHashMap());
    }

    public Row(Map<String, Object> data) {
        this.data = newHashMap(data);
        //this.joinedRows = Maps.newLinkedHashMap();
    }

    public Row addKeyVals(Object... keyVals) {
        checkArgument(
                keyVals.length % 2 == 0, "content must be a list of iterant key value pairs.");

        Iterable<List<Object>> partition = Iterables.partition(newArrayList(keyVals), 2);
        for (List<Object> objects : partition) {
            Object key = objects.get(0);
            checkArgument(key instanceof String, "argument " + key + " is not a String");
            Object value = objects.get(1);
            put(key.toString(), value);
        }
        return this;
    }

    @Override
    public Object put(String key, Object value) {
        return super.put(key, value);
    }

    public List<String> getKeyList() {
        return Lists.newLinkedList(keySet());
    }

    public <A> A getAs(Class<A> type, String key) {
        if (!containsKey(key)) {
            throw new IllegalArgumentException("No entry for key '" + key + "'.");
        }
        Object value = get(key);
        if (!type.isAssignableFrom(value.getClass())) {
            throw new ClassCastException(
                    "Cannot cast value '" + value + "' of type '" + value.getClass() + "' to '" +
                            type + "'");
        }
        return type.cast(value);
    }

    public Optional<Object> getOptional(String key) {
        if (containsKey(key)) {
            return Optional.of(get(key));
        } else {
            return Optional.absent();
        }
    }

    public int getInt(String key) {
        return getAs(Integer.class, key);
    }

    public Double getDouble(String key) {
        return getAs(Double.class, key);
    }

    public Float getFloat(String key) {
        return getAs(Float.class, key);
    }

    public Boolean getBoolean(String key) {
        return getAs(Boolean.class, key);
    }

    public DateTime getDateTime(String key) {
        return getAs(DateTime.class, key);
    }

    public String getString(String key) {
        return getAs(String.class, key);
    }

    public byte[] getByteArray(String key) {
        return getAs(byte[].class, key);
    }

    @SuppressWarnings("unchecked")
    public List<Row> getJoinedRows(String joinedTabledName) {
        //return joinedRows.get(joinedTabledName);
        return getAs(List.class, "__" + joinedTabledName);
    }

    public void addJoinedRows(String tableName, LinkedList<Row> joinedForeignRows) {
        //joinedRows.put(tableName, joinedForeignRows);
        data.put("__" + tableName, joinedForeignRows);
    }

    public Row copy() {
        return new Row(data);
    }

    public Row extractMap(String... keys) {
        Row extracted = new Row();
        for (String key : keys) {
            extracted.put(key, get(key));
        }
        return extracted;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("data", delegate()).toString();
    }

    @Override
    protected Map<String, Object> delegate() {
        return data;
    }

}
