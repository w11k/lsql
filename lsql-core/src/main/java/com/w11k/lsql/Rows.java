package com.w11k.lsql;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.ForwardingList;
import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.base.Optional.of;

public class Rows extends ForwardingList<Row> {

    private final List<Row> rows;

    public Rows(List<Row> rows) {
        this.rows = rows;
    }

    public Optional<Row> first() {
        return this.rows.size() > 0 ? of(rows.get(0)) : Optional.<Row>absent();
    }

    public Rows filter(Predicate<Row> fn) {
        List<Row> list = Lists.newLinkedList();
        for (Row row : rows) {
            if (fn.apply(row)) {
                list.add(row);
            }
        }
        return new Rows(list);
    }

    public <T> List<T> map(Function<Row, T> fn) {
        List<T> list = Lists.newLinkedList();
        for (Row row : rows) {
            T val = fn.apply(row);
            list.add(val);
        }
        return list;
    }

    @Override
    protected List<Row> delegate() {
        return rows;
    }
}
