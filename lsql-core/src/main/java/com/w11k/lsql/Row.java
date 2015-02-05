package com.w11k.lsql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 *
 */
public class Row extends ForwardingMap<String, Object> {

    private Map<String, Object> data;

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
    }

    public void setDelegate(Map<String, Object> data) {
        this.data = data;
    }

    public Row addKeyVals(Object... keyVals) {
        checkArgument(
                keyVals.length == 0 || keyVals.length % 2 == 0, "content must be a list of iterant key value pairs.");

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
        if (value == null) {
            return null;
        }
        if (!type.isAssignableFrom(value.getClass())) {
            A converted = convertWithJackson(type, value);
            put(key, converted);
            return converted;
        }
        return type.cast(value);
    }

    public Optional<Object> getOptional(String key) {
        return Optional.fromNullable(get(key));
    }

    public Integer getInt(String key) {
        return getAs(Integer.class, key);
    }

    public Long getLong(String key) {
        return getAs(Long.class, key);
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
        Object val = get(key);
        return val == null ? null : new DateTime(get(key));
    }

    public String getString(String key) {
        return getAs(String.class, key);
    }

    public Row getRow(String key) {
        return getAs(Row.class, key);
    }

    public Blob getBlob(String key) {
        return getAs(Blob.class, key);
    }

    public byte[] getByteArray(String key) {
        Blob blob = getBlob(key);
        return blob == null ? null : blob.getData();
    }

    @SuppressWarnings("unchecked")
    public <A> List<A> getListOf(Class<A> clazz, String key) {
        return (List<A>) get(key);
    }

    @SuppressWarnings("unchecked")
    public <A> Set<A> getSetOf(Class<A> clazz, String key) {
        return (Set<A>) get(key);
    }

    @SuppressWarnings("unchecked")
    public <A> TreeSet<A> getTreeSetOf(Class<A> clazz, String key) {
        return (TreeSet<A>) get(key);
    }

    @SuppressWarnings("unchecked")
    public <T extends Row> List<T> getJoined(String key) {
        return (List<T>) getListOf(Row.class, key);
    }

    public List<Row> getJoinedRows(String key) {
        return getListOf(Row.class, key);
    }

    public Row pick(String... keys) {
        Row extracted = new Row();
        for (String key : keys) {
            extracted.put(key, get(key));
        }
        return extracted;
    }

    public Row copy() {
        Row copy = new Row();
        copy.putAll(this);
        return copy;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("content", delegate()).toString();
    }

    protected ObjectMapper getObjectMapper() {
        return LSql.OBJECT_MAPPER;
    }

    @Override
    protected Map<String, Object> delegate() {
        return data;
    }

    private <A> A convertWithJackson(Class<A> expectedType, Object value) {
        ObjectMapper mapper = getObjectMapper();
        String valString = "\"" + value + "\"";
        try {
            JsonNode rootNode = mapper.readValue(valString, JsonNode.class);
            return mapper.treeToValue(rootNode, expectedType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
