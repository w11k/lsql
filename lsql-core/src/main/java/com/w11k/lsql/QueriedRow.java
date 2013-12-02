package com.w11k.lsql;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.util.Map;

public class QueriedRow extends LinkedRow {

    private final LSql lSql;

    private final Map<String, Column> columns;

    public QueriedRow(LSql lSql, Map<String, Object> rowData, Map<String, Column> columnsByName) {
        super(null, rowData);
        this.columns = columnsByName;
        this.lSql = lSql;
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
    public Map<String, LinkedRow> groupByTables() {
        Map<String, LinkedRow> byTables = Maps.newHashMap();

        for (String key : keySet()) {
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
