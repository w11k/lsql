package com.w11k.lsql.query;

import com.google.common.collect.Maps;
import com.w11k.lsql.LSql;
import com.w11k.lsql.ResultSetColumn;
import com.w11k.lsql.Row;
import com.w11k.lsql.converter.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryToTreeConverter {

    private static class TableSegment {
        private int firstColumn;

        private int lastColumn;
    }

    private static class MarkerColumnValue {

        private final String path;

        private final Object value;

        public MarkerColumnValue(String path, Object value) {
            this.path = path;
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MarkerColumnValue that = (MarkerColumnValue) o;
            return path.equals(that.path) && value.equals(that.value);

        }

        @Override
        public int hashCode() {
            int result = path.hashCode();
            result = 31 * result + value.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "MarkerColumnValue{" +
                    "path='" + path + '\'' +
                    ", value=" + value +
                    '}';
        }
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LSql lSql;

    private final AbstractQuery<?> query;

    public QueryToTreeConverter(AbstractQuery<?> query) {
        this.query = query;
        this.lSql = query.getlSql();
    }

    public List<Row> getTree() {
        try {
            return buildTree();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<Row> buildTree()
            throws SQLException {

        ResultSet resultSet = this.query.getPreparedStatement().executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        // Table segments (first column -> last column)
        LinkedHashMap<Integer, Integer> tableSegments = Maps.newLinkedHashMap();

        // Normal columns
        Map<Integer, ResultSetColumn> columns = Maps.newLinkedHashMap();

        // Build table segments and columns meta data
        int lastMarkerColumn = 1;
        columns.put(1, new ResultSetColumn(1, metaData.getColumnLabel(1), new MarkerColumnConverter()));
        for (int i = 2; i <= metaData.getColumnCount(); i++) {
            String label = metaData.getColumnLabel(i);
            if (isColumnMarker(label)) {
                tableSegments.put(lastMarkerColumn, i - 1);
                lastMarkerColumn = i;
                columns.put(i, new ResultSetColumn(i, label, new MarkerColumnConverter()));
            } else {
                Converter converter = this.query.getConverterForResultSetColumn(metaData, i, label, true);
                columns.put(i, new ResultSetColumn(i, label, converter));
            }
        }
        tableSegments.put(lastMarkerColumn, metaData.getColumnCount());

        // Iterate rows
        while (resultSet.next()) {
            // Get marker column values
            for (Map.Entry<Integer, Integer> segment : tableSegments.entrySet()) {
                ResultSetColumn rsc = columns.get(segment.getKey());

                Object value = rsc.getConverter().getValueFromResultSet(this.lSql, resultSet, segment.getKey());
                String name = rsc.getName();

                System.out.println("name = " + name);
                System.out.println("value = " + value);
            }

            for (Map.Entry<Integer, Integer> segment : tableSegments.entrySet()) {
                for (int i = segment.getKey(); i <= segment.getValue(); i++) {
                    ResultSetColumn rsc = columns.get(i);
                }
            }



        }


        // marker column
//                    if (markerColumnConverters.containsKey(columnIdx)) {
//                        try {
//                            String label = column.getName();
//                            MarkerColumnValue mcv = new MarkerColumnValue(
//                                    label,
//                                    column.getConverter().getValueFromResultSet(
//                                            query.getlSql(), resultSet, columnIdx));
//
//                            int depth = CharMatcher.is('/').countIn(label);
//                            List<String> pathsCopy = newLinkedList(paths).subList(0, depth - 1);
//
//
//                            System.out.println("depth = " + depth);
//                            System.out.println("mcv = " + mcv);
//
//
//                        } catch (/*SQL*/Exception e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
        // field
//                    else {
//
//                    }

//                    try {
//                        setValue(
//                                entity,
//                                column.getName(),
//                                column.getConverter().getValueFromResultSet(lSql, resultSet, column.getPosition()));
//                    } catch (SQLException e) {
//                        throw new RuntimeException(e);
//                    }

        return null;


    }

//    private LinkedHashMap<Integer, Converter> extractMarkerColumns(ResultSetMetaData metaData)
//            throws SQLException {
//
//        LinkedHashMap<Integer, Converter> markerColumnConverters = Maps.newLinkedHashMap();
//        for (int i = 1; i <= metaData.getColumnCount(); i++) {
//            String rawLabel = metaData.getColumnLabel(i);
//
//            if (isColumnMarker(rawLabel)) {
//                markerColumnConverters.put(i, new MarkerColumnConverter());
//            }
//        }
//        return markerColumnConverters;
//    }

    private boolean isColumnMarker(String rawLabel) {
        return rawLabel.trim().startsWith("/");
    }

}
