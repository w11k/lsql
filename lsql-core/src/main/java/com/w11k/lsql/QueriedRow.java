package com.w11k.lsql;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.util.Map;

public class QueriedRow extends LinkedRow {

    private final Map<String, Column> columns;

    public QueriedRow(Map<String, Object> rowData, Map<String, Column> columnByName) {
        super(null, rowData);
        this.columns = columnByName;
    }

    public Optional<?> save() {
        failIfTableIsMissing();
        return super.save();
    }

    public void delete() {
        failIfTableIsMissing();
        super.delete();
    }

    public boolean hasLinkedTable() {
        return getTable() != null;
    }

    /**
     * Group this row by table origin. Calculated values (e.g. 'count(*)') are removed.
     */
    public Map<String, LinkedRow> groupByTables() {
        Map<String, LinkedRow> byTables = Maps.newHashMap();
        for (String key : columns.keySet()) {
            Column column = columns.get(key);
            if (column.hasCorrespondingTable()) {
                String tableName = column.getTable().getTableName();
                if (!byTables.containsKey(tableName)) {
                    byTables.put(tableName, new LinkedRow(column.getTable()));
                }
                Row row = byTables.get(tableName);
                row.put(column.getColumnName(), get(key));
            }
        }
        return byTables;
    }

    private void failIfTableIsMissing() {
        if (!hasLinkedTable()) {
            throw new IllegalStateException("This QueriedRow is not linked to a table.");
        }
    }


}
