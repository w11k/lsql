package com.w11k.lsql;

import com.google.common.base.Function;
import com.google.common.collect.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class QueriedRows extends ForwardingList<QueriedRow> {

    private List<QueriedRow> rows;

    public QueriedRows(List<QueriedRow> rows) {
        this.rows = rows;
    }

    public Map<Object, QueriedRows> groupByColumn(final String columnName) {
        ListMultimap<Object, QueriedRow> byColumn = Multimaps.index(rows, new Function<QueriedRow, Object>() {
            public Object apply(QueriedRow input) {
                return input.get(columnName);
            }
        });
        return Maps.transformValues(byColumn.asMap(), new Function<Collection<QueriedRow>, QueriedRows>() {
            public QueriedRows apply(Collection<QueriedRow> rows) {
                return new QueriedRows(Lists.newLinkedList(rows));
            }
        });
    }

    @Override
    protected List<QueriedRow> delegate() {
        return rows;
    }
}
