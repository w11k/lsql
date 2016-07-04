package com.w11k.lsql;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class ListLiteralQueryParameter<T> implements LiteralQueryParameter {

    public static <A> ListLiteralQueryParameter<A> of(Iterable<A> values) {
        return new ListLiteralQueryParameter<A>(values);
    }

    public static <A> ListLiteralQueryParameter<A> of(A... values) {
        return new ListLiteralQueryParameter<A>(Lists.newArrayList(values));
    }

    protected List<T> values;

    public ListLiteralQueryParameter(Iterable<T> values) {
        this.values = Lists.newCopyOnWriteArrayList(values);
    }

    @Override
    public String getSqlString() {
        if (getNumberOfQueryParameters() == 0) {
            return "";
        }
        return Strings.repeat("?,", getNumberOfQueryParameters() - 1) + "?";
    }

    @Override
    public int getNumberOfQueryParameters() {
        return this.values.size();
    }

    @Override
    public void set(PreparedStatement ps, int preparedStatementIndex, int localIndex) throws SQLException {
        ps.setObject(preparedStatementIndex, this.values.get(localIndex));
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + Joiner.on(",").join(values) + "]";
    }
}
