package com.w11k.lsql.utils;

import com.w11k.lsql.Row;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Maps.newHashMap;

public class RowUtils {

    public static <K> Map<K, Row> groupListOfRowsByUniqueKey(List<? extends Row> rows, String key, Class<K> keyType) {
        Map<K, Row> map = newHashMap();
        for (Row row : rows) {
            K keyValue = row.getAs(keyType, key);
            if (map.containsKey(keyValue)) {
                throw new IllegalStateException("Dublicate row for key value '" + keyValue + "'");
            }
            map.put(keyValue, row);
        }
        return map;
    }

}
