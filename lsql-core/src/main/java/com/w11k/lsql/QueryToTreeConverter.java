package com.w11k.lsql;

import com.google.common.collect.Maps;
import com.w11k.lsql.converter.Converter;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

class QueryToTreeConverter {

    private final Query query;
    private final ResultSet resultSet;
    private final ResultSetMetaData metaData;

    private Map<Integer, ResultSetColumn> resultSetColumns = Maps.newHashMap();

    public QueryToTreeConverter(Query query) {
        this.query = query;

        try {
            this.resultSet = query.getPreparedStatement().executeQuery();
            this.metaData = resultSet.getMetaData();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private ResultSetColumn getConverter(int columnNumber) {
        if (resultSetColumns.containsKey(columnNumber)) {
            return resultSetColumns.get(columnNumber);
        }






//        String columnLabel = query.getlSql().getDialect().identifierSqlToJava(metaData.getColumnLabel(i));
//                Converter converter = converters.containsKey(columnLabel)
//                  ? converters.get(columnLabel)
//                  : getConverter(metaData, i);
//                resultSetColumns.add(new ResultSetColumn(i, columnLabel, converter));


    }

    private void createTree() {
        try {

//            for (int i = 1; i <= metaData.getColumnCount(); i++) {
//
//            }

            while (resultSet.next()) {


            }
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Object getTree() {
        return null;
    }

}
