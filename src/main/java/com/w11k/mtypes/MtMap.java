package com.w11k.mtypes;

import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class MtMap extends ForwardingMap<String, Object> {

    private final Mt mt;

    private final Map<String, Object> data;

    private final Optional<String> typeName;

    public MtMap(Mt mt, Optional<String> typeName) {
        this.mt = mt;
        this.typeName = typeName;
        data = Maps.newHashMap();
    }

    public MtMap(Mt mt, Optional<String> typeName, Map<String, Object> data) {
        this.mt = mt;
        this.typeName = typeName;
        this.data = data;
    }

    public <A> A getAs(Class<A> type, String key) {
        return mt.convertTo(type, get(key));
    }

    public int getInt(String key) {
        return getAs(Integer.class, key);
    }

    public String getString(String key) {
        return getAs(String.class, key);
    }

    public MtMap addKeyVals(Object... keyVals) {
        checkArgument(keyVals.length % 2 == 0, "content must be a list of key value entries.");

        Iterable<List<Object>> partition = Iterables.partition(Lists.newArrayList(keyVals), 2);
        for (List<Object> objects : partition) {
            Object key = objects.get(0);
            checkArgument(key instanceof String, "argument " + key + " is not a String");
            put(key.toString(), objects.get(1));
        }
        return this;
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
