package com.w11k.lsql;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class LMap extends ForwardingMap<String, Object> {

    // Static Constructor Methods

    public static LMap create() {
        return new LMap(Optional.<String>absent(), Maps.<String, Object>newHashMap());
    }

    public static LMap create(String typeName) {
        return new LMap(Optional.of(typeName), Maps.<String, Object>newHashMap());
    }

    public static LMap fromMap(Map<String, Object> data) {
        return new LMap(Optional.<String>absent(), data);
    }

    public static LMap fromMap(String typeName, Map<String, Object> data) {
        return new LMap(Optional.of(typeName), data);
    }

    public static LMap fromKeyVals(Object... keyVals) {
        checkArgument(keyVals.length % 2 == 0, "content must be a list of iterant key value pairs.");
        LMap lMap = LMap.create();

        Iterable<List<Object>> partition = Iterables.partition(Lists.newArrayList(keyVals), 2);
        for (List<Object> objects : partition) {
            Object key = objects.get(0);
            checkArgument(key instanceof String, "argument " + key + " is not a String");
            lMap.put(key.toString(), objects.get(1));
        }
        return lMap;
    }

    // Instance members

    private TypeConverter typeConverter = new TypeConverter();

    private final Map<String, Object> data;

    private final Optional<String> typeName;

    private LMap(Optional<String> typeName, Map<String, Object> data) {
        this.typeName = typeName;
        this.data = data;
    }

    public TypeConverter getTypeConverter() {
        return typeConverter;
    }

    public void setTypeConverter(TypeConverter typeConverter) {
        this.typeConverter = typeConverter;
    }

    public <A> A getAs(Class<A> type, String key) {
        return typeConverter.convert(type, get(key)).get();
    }

    public int getInt(String key) {
        return getAs(Integer.class, key);
    }

    public DateTime getDateTime(String key) {
        return getAs(DateTime.class, key);
    }

    public String getString(String key) {
        return getAs(String.class, key);
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
