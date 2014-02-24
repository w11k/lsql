package com.w11k.lsql;

import com.google.common.base.Function;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.collect.Multimaps.index;

public class IdGroupedRowCreator {

    public static List<QueriedRow> create(final List<String> ids, List<QueriedRow> queriedRows) {
        final String id = getColumnName(ids.get(0));
        ids.remove(0);

        List<QueriedRow> result = Lists.newLinkedList();
        ListMultimap<Object, QueriedRow> byColumn = groupBycolumn(queriedRows, id);
        for (Object key : byColumn.keySet()) {
            List<QueriedRow> rowsForKey = byColumn.get(key);
            QueriedRow root = rowsForKey.get(0);
            if (!ids.isEmpty()) {
                String childId = getJoinedEntriesName(ids.get(0));
                List<QueriedRow> childs = create(Lists.newLinkedList(ids), rowsForKey);
                root.put(childId, childs);
            }
            result.add(root);
        }
        return result;
    }

    private static String getJoinedEntriesName(String s) {
        int index = s.indexOf(" as ");
        if (index == -1) {
            return s + "s";
        } else {
            return s.substring(index + 4, s.length());
        }
    }

    private static String getColumnName(String s) {
        int index = s.indexOf(" as ");
        if (index == -1) {
            return s;
        } else {
            return s.substring(0, index);
        }
    }

    private static ListMultimap<Object, QueriedRow> groupBycolumn(List<QueriedRow> queriedRows, final String column) {
        return index(queriedRows, new Function<QueriedRow, Object>() {
            public Object apply(QueriedRow input) {
                return input.get(column);
            }
        });
    }


}
