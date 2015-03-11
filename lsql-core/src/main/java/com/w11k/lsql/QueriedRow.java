package com.w11k.lsql;

import com.google.common.base.Optional;

import java.util.Collection;
import java.util.Map;

import static com.google.common.base.Optional.of;

public class QueriedRow extends Row {

    private final Map<String, ResultSetColumn<?>> resultSetColumns;

    public QueriedRow(Map<String, ResultSetColumn<?>> resultSetColumns) {
        this.resultSetColumns = resultSetColumns;
    }

    public Map<String, ResultSetColumn<?>> getResultSetColumns() {
        return resultSetColumns;
    }

    public LinkedRow extractLinkedRowByColumn(String columnName) {
        Optional<Table> tableForColumn = findTableForColumn(columnName);
        if (!tableForColumn.isPresent()) {
            throw new IllegalArgumentException("column not in result set or column does not belong to a table");
        }

        Table table = tableForColumn.get();
        LinkedRow linkedRow = table.newLinkedRow();
        for (ResultSetColumn<?> column : resultSetColumns.values()) {
            Optional<? extends Table> t = column.getColumn().getTable();
            if (t.isPresent() && t.get().equals(table)) {
                linkedRow.put(column.getColumn().getColumnName(), get(column.getName()));
            }
        }
        return linkedRow;
    }

    private Optional<Table> findTableForColumn(String columnName) {
        Collection<ResultSetColumn<?>> values = resultSetColumns.values();
        for (ResultSetColumn<?> column : values) {
            String nameInResultSet = column.getName();
            if (nameInResultSet.equals(columnName)) {
                return of(column.getColumn().getTable().get());
            }
        }
        return Optional.absent();
    }
}
