package com.w11k.lsql;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.*;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.joda.time.DateTime;

import java.io.IOException;
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

    public Row addKeyVals(Object... keyVals) {
        checkArgument(
                keyVals.length == 0 ||
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

    public Blob getBlob(String key) {
        return getAs(Blob.class, key);
    }

    public byte[] getByteArray(String key) {
        return getBlob(key).getData();
    }

    public Row extractNewMap(String... keys) {
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

    protected ObjectMapper getObjectMapper() {
        return new ObjectMapper();
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
