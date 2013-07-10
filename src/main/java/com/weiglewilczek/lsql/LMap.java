package com.weiglewilczek.lsql;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class LMap extends ForwardingMap<String, Object> {

    public static LMap fromKeyVals(Object... keyVals) {
        LMap lMap = new LMap();
        Preconditions.checkArgument(keyVals.length % 2 == 0, "content must be a list of key value entries.");
        Iterable<List<Object>> partition = Iterables.partition(Lists.newArrayList(keyVals), 2);
        for (List<Object> objects : partition) {
            Object key = objects.get(0);
            Preconditions.checkArgument(key instanceof String, "argument " + key + " is not a String");
            lMap.put(key.toString(), objects.get(1));
        }
        return lMap;
    }

    private final Map<String, Object> data;

    private final TypesConverter typesConverter = new TypesConverter();

    public LMap() {
        data = Maps.newHashMap();
    }

    public LMap(Map<String, Object> data) {
        this.data = data;
    }

    public int getInt(String key) {
        return getAs(Integer.class, key);
    }

    public String getString(String key) {
        return getAs(String.class, key);
    }

    public <A> A getAs(Class<A> type, String key) {
        return typesConverter.convert(type, get(key)).get();
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
