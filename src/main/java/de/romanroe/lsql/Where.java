package de.romanroe.lsql;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

public class Where {

    private final SelectStatement selectStatement;
    private final String select;

    public Where(SelectStatement selectStatement, String select) {
        this.selectStatement = selectStatement;
        this.select = select;
    }

    public <T> List<T> map(Function<Row, T> function) {
        List<T> list = Lists.newLinkedList();
        Statement st = selectStatement.getTable().getlSql().createStatement();
        try {
            final ResultSet resultSet = st.executeQuery(select);
            boolean hasNext = resultSet.next();
            while (hasNext) {
                list.add(function.apply(new Row(resultSet)));
                hasNext = resultSet.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

}
