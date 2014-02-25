package com.w11k.lsql;

import com.google.common.base.Optional;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.List;
import java.util.Map;

import static com.google.common.collect.Lists.newLinkedList;

public class IdGroupedRowCreator {

    public static List<QueriedRow> create(final List<String> ids, List<QueriedRow> queriedRows) {
        final String id = getColumnName(ids.get(0));
        ids.remove(0);

        List<QueriedRow> result = newLinkedList();
        ListMultimap<Object, QueriedRow> byColumn = groupByColumn(queriedRows, id);
        for (Object key : byColumn.keySet()) {
            List<QueriedRow> rowsForKey = byColumn.get(key);
            QueriedRow root = rowsForKey.get(0);

            // Create copy
            QueriedRow copy = new QueriedRow(root.getResultSetColumns());
            copy.putAll(root);
            removeColumnWithDifferentTable(copy, id);

            if (!ids.isEmpty()) {
                String childId = getJoinedEntriesName(ids.get(0));
                List<QueriedRow> childs = create(newLinkedList(ids), rowsForKey);
                copy.put(childId, childs);
            }
            result.add(copy);
        }
        return result;
    }

    /**
     * Remove columns with a different table source. Keep column if the table is unknown.
     */
    private static void removeColumnWithDifferentTable(QueriedRow row, String column) {
        Map<String, ResultSetColumn<?>> rscs = row.getResultSetColumns();
        ResultSetColumn<?> rsc = rscs.get(column);
        Optional<? extends Table<?>> expectedTable = rsc.getColumn().getTable();
        if (!expectedTable.isPresent()) {
            return;
        }
        for (String key : newLinkedList(row.keySet())) {
            if (!rscs.containsKey(key)) {
                // Remove joined entries
                row.remove(key);
            } else {
                Optional<? extends Table<?>> toCheck = rscs.get(key).getColumn().getTable();
                if (toCheck.isPresent() && !expectedTable.equals(toCheck)) {
                    row.remove(key);
                }
            }
        }
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

    private static ListMultimap<Object, QueriedRow> groupByColumn(List<QueriedRow> queriedRows,
                                                                  final String column) {
        LinkedListMultimap<Object, QueriedRow> result = LinkedListMultimap.create();
        for (QueriedRow row : queriedRows) {
            Object value = row.get(column);
            if (value != null) {
                result.put(value, row);
            }
        }
        return result;
    }

}
