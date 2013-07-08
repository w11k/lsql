package de.romanroe.lsql;

import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class L {

    public static Map<String, Object> createMap(Object... content) {
        Preconditions.checkArgument(content.length % 2 == 0, "content must be a list of key value entries.");
        Map<String, Object> toReturn = Maps.newHashMap();
        Iterable <List<Object>> partition = Iterables.partition(Lists.newArrayList(content), 2);
        for (List<Object> objects : partition) {
            Object key = objects.get(0);
            Preconditions.checkArgument(key instanceof String, "argument " + key + " is not a String");
            toReturn.put(key.toString(), objects.get(1));
        }
        return toReturn;
    }
}
