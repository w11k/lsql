package com.w11k.lsql;

import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

public class QueriedRow extends LinkedRow {

    private final List<Query.ResultSetColumn> columns;

    public QueriedRow(Map<String, Object> rowData, List<Query.ResultSetColumn> columnsByName) {
        super(null, rowData);
        this.columns = columnsByName;
    }

    @Override
    public Object put(String key, Object value) {
        failIfTableIsMissing();
        return super.put(key, value);
    }

    @Override
    public Optional<?> save() {
        failIfTableIsMissing();
        return super.save();
    }

    @Override
    public void delete() {
        failIfTableIsMissing();
        super.delete();
    }

    public boolean hasLinkedTable() {
        return getTable() != null;
    }

    /**
     * Group this row by table origin. Calculated values (e.g. 'count(*)') are stored with an empty table name ''.
     */
    public Map<String, Map<Object, LinkedRow>> groupByTables() {
        Map<String, Map<String, LinkedRow>> byTables = Maps.newHashMap();


        for (Query.ResultSetColumn rsc : columns) {
            Column column = rsc.column;
            String tableName = column.getTableName().or("");
            String tableIndex = "1";
            if (CharMatcher.anyOf(".").countIn(rsc.nameInRow) == 2) {
                tableIndex = rsc.nameInRow.substring(tableName.length() + 1, rsc.nameInRow.lastIndexOf('.'));
            }
            if (!byTables.containsKey(tableName)) {
                byTables.put(tableName, Maps.<String, LinkedRow>newLinkedHashMap());
            }
            if (!byTables.get(tableName).containsKey(tableIndex)) {
                byTables.get(tableName).put(tableIndex, column.getTable().get().newLinkedRow());
            }
            byTables.get(tableName).get(tableIndex).put(column.getColumnName(), get(rsc.nameInRow));
        }

        return Maps
                .transformValues(byTables, new Function<Map<String, LinkedRow>, Map<Object, LinkedRow>>() {
                    public Map<Object, LinkedRow> apply(Map<String, LinkedRow> input) {
                        Map<Object, LinkedRow> entry = Maps.newLinkedHashMap();
                        for (String key : input.keySet()) {
                            LinkedRow row = input.get(key);
                            if (row.getId() != null) {
                                entry.put(row.getId(), row);
                            }
                        }
                        return entry;
                    }
                });
    }

    private void failIfTableIsMissing() {
        if (!hasLinkedTable()) {
            throw new IllegalStateException("This QueriedRow is not linked to a table.");
        }
    }


}
