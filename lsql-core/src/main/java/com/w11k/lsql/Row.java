package com.w11k.lsql;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newArrayList;
import static com.google.common.collect.Maps.newHashMap;

/**
 *
 */
public class Row extends ForwardingMap<String, Object> {

    @Deprecated
    public static boolean LEGACY_CONVERT_VALUE_ON_GET = false;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static Row fromKeyVals(Object... keyVals) {
        Row r = new Row();
        r.addKeyVals(keyVals);
        return r;
    }

    private Map<String, Object> data;

    public Row() {
        this(Maps.<String, Object>newHashMap());
    }

    public Row(Map<String, Object> data) {
        this.data = newHashMap(data);
    }

//    public void setDelegate(Map<String, Object> data) {
//        this.data = data;
//    }

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

    public <A> A getAs(Class<A> type, String key) {
        if (!LEGACY_CONVERT_VALUE_ON_GET) {
            return getAs(type, key, false);
        }

        // legacy mode
        try {
            return getAs(type, key, false);
        } catch (WrongTypeException e) {
            String msg = "Row entry for " +
                    "key: '" + key + "' " +
                    "was converted implicitly to type: " +
                    "'" + type.getCanonicalName() + "'. " +
                    "Please use .getAs(type, key, true);";
            logger.warn(msg);
            return getAs(type, key, true);
        }
    }

    public <A> A getAs(Class<A> type, String key, boolean convert) {
        if (!containsKey(key)) {
            throw new IllegalArgumentException("No entry for key '" + key + "'.");
        }
        Object value = get(key);
        if (value == null) {
            return null;
        }

        if (type.isAssignableFrom(value.getClass())) {
            return type.cast(value);
        } else if (convert) {
            try {
                return LSql.OBJECT_MAPPER.convertValue(value, type);
            } catch (Exception e) {
                throw new IllegalArgumentException("Row entry for " +
                        "key: '" + key + "', " +
                        "value: '" + value + "', " +
                        "type: '" + value.getClass().getCanonicalName() + "' " +
                        "can not be converted to type '" + type.getCanonicalName() + "'",
                        e);
            }
        } else {
            throw new WrongTypeException("Row entry for " +
                    "key: '" + key + "', " +
                    "value: '" + value + "', " +
                    "type: '" + value.getClass().getCanonicalName() + "' " +
                    "can not be converted to type '" + type.getCanonicalName() + "'");
        }
    }

    public <A> A getAsOr(Class<A> type, String key, A defaultValue) {
        if (!containsKey(key)) {
            return defaultValue;
        }
        return getAs(type, key);
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
    public <A> List<A> getAsListOf(Class<A> clazz, String key) {
        return (List<A>) get(key);
    }

    @SuppressWarnings("unchecked")
    public <A> Set<A> getAsSetOf(Class<A> clazz, String key) {
        return (Set<A>) get(key);
    }

    public boolean hasNonNullValue(String key) {
        return get(key) != null;
    }

    public Row pick(String... keys) {
        Row extracted = new Row();
        for (String key : keys) {
            if (containsKey(key)) {
                extracted.put(key, get(key));
            }
        }
        return extracted;
    }

    public Row putAllIfAbsent(Map<String, Object> source) {
        Row copiedValues = new Row();
        for (String key : source.keySet()) {
            if (!containsKey(key)) {
                Object value = source.get(key);
                put(key, value);
                copiedValues.put(key, value);
            }
        }
        return copiedValues;
    }

    public Row copy() {
        Row copy = new Row();
        copy.putAll(this);
        return copy;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).addValue(delegate()).toString();
    }

    @Override
    protected Map<String, Object> delegate() {
        return data;
    }

}
