package com.w11k.lsql;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class QueriedRows extends ForwardingList<QueriedRow> {

    private final List<QueriedRow> rows;

    public QueriedRows(List<QueriedRow> rows) {
        this.rows = rows;
    }

    public <T> List<T> map(Function<QueriedRow, T> rowHandler) {
        List<T> list = Lists.newLinkedList();
        for (QueriedRow row : this) {
            list.add(rowHandler.apply(row));
        }
        return list;
    }

    public Optional<QueriedRow> getFirstRow() {
        if (rows.size() == 0) {
            return absent();
        } else {
            return of(rows.get(0));
        }
    }

    public List<RowPojo> asViewTree(final String... ids) {
        return QueriedRowsToTreeCreator.createViewTree(Lists.newArrayList(ids), rows);
    }

    public <T extends RowPojo> List<T> asResolvedTree(final String... ids) {
        return QueriedRowsToTreeCreator.createResolvedTree(Lists.newArrayList(ids), rows);
    }

    @Override
    protected List<QueriedRow> delegate() {
        return rows;
    }
}
