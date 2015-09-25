package com.w11k.lsql.utils;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.w11k.lsql.Row;

import java.util.List;
import java.util.Map;

public class RowUtils {

    public static <K> Map<K, List<Row>> groupRowsByKey(List<? extends Row> rows, Function<Row, K> keyFunction) {
        Map<K, List<Row>> map = Maps.newHashMap();
        for (Row row : rows) {
            K keyValue = keyFunction.apply(row);
            if (!map.containsKey(keyValue)) {
                map.put(keyValue, Lists.<Row>newLinkedList());
            }
            map.get(keyValue).add(row);
        }
        return map;
    }

    public static <K> Map<K, Row> groupRowsByUniqueKeyIgnoreDuplicates(List<? extends Row> rows, Function<Row, K> keyFunction) {
        Map<K, Row> map = Maps.newHashMap();
        for (Row row : rows) {
            K keyValue = keyFunction.apply(row);
            map.put(keyValue, row);
        }
        return map;
    }

}
