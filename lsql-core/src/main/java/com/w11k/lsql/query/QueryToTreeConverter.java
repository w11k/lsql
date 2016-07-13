package com.w11k.lsql.query;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.w11k.lsql.LSql;
import com.w11k.lsql.ResultSetColumn;
import com.w11k.lsql.converter.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static com.google.common.collect.Lists.newLinkedList;

public class QueryToTreeConverter {

    private static class SegmentHeader {
        private int firstColumn;

        private int lastColumn;

        public SegmentHeader(int firstColumn, int lastColumn) {
            this.firstColumn = firstColumn;
            this.lastColumn = lastColumn;
        }

        public int getFirstColumn() {
            return firstColumn;
        }

        public int getLastColumn() {
            return lastColumn;
        }
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

    private final EntityCreator entityCreator;

    private final AbstractQuery<?> query;

    public QueryToTreeConverter(AbstractQuery<?> query,
                                EntityCreator entityCreator) {
        this.query = query;
        this.lSql = query.getlSql();
        this.entityCreator = entityCreator;
    }

    public List<?> getTree() {
        try {
            return buildTree();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<?> buildTree()
            throws SQLException {

        ResultSet resultSet = this.query.getPreparedStatement().executeQuery();
        ResultSetMetaData metaData = resultSet.getMetaData();

        // Table segments
        TreeMap<String, SegmentHeader> segmentHeaders = Maps.newTreeMap();

        // Normal columns
        Map<Integer, ResultSetColumn> columns = Maps.newLinkedHashMap();

        // Build table segments and columns meta data
        int lastMarkerIndex = 1;
        String lastLabel = metaData.getColumnLabel(lastMarkerIndex).replaceAll(" ", "");
        assert isColumnMarker(lastLabel);
        columns.put(1, new ResultSetColumn(1, lastLabel, new MarkerColumnConverter()));
        for (int i = 2; i <= metaData.getColumnCount(); i++) {
            String label = metaData.getColumnLabel(i);
            if (isColumnMarker(label)) {
                label = label.replaceAll(" ", "");
                columns.put(i, new ResultSetColumn(i, label, new MarkerColumnConverter()));
                assert !segmentHeaders.containsKey(lastLabel);
                segmentHeaders.put(lastLabel, new SegmentHeader(lastMarkerIndex, i - 1));
                lastLabel = label;
                lastMarkerIndex = i;
            } else {
                label = lSql.identifierSqlToJava(label);
                Converter converter = this.query.getConverterForResultSetColumn(metaData, i, label, true);
                columns.put(i, new ResultSetColumn(i, label, converter));
            }
        }
        segmentHeaders.put(lastLabel, new SegmentHeader(lastMarkerIndex, metaData.getColumnCount()));

        // Entities
        Map<List<MarkerColumnValue>, Object> entities = Maps.newHashMap();
        List<Object> topLevelRows = Lists.newLinkedList();

        // Iterate rows
        while (resultSet.next()) {
            TreeMap<String, Object> markerColumnValues = Maps.newTreeMap();

            // First get marker column values to be independent from the marker ordering
            for (Map.Entry<String, SegmentHeader> segment : segmentHeaders.entrySet()) {
                int firstColumnIndex = segment.getValue().getFirstColumn();
                ResultSetColumn rsc = columns.get(firstColumnIndex);

                String path = rsc.getName();
                Object value = rsc.getConverter().getValueFromResultSet(this.lSql, resultSet, firstColumnIndex);

                markerColumnValues.put(path, value);
            }

            // Iterate segments and create entities
            for (Map.Entry<String, SegmentHeader> segment : segmentHeaders.entrySet()) {

                // Has current row data for this segment?
                if (markerColumnValues.get(segment.getKey()) == null) {
                    continue;
                }

                // create entity for current segment
                List<MarkerColumnValue> fullPath = getFullPath(segment, markerColumnValues);
                if (!entities.containsKey(fullPath)) {
                    List<MarkerColumnValue> parentPath = fullPath.subList(0, fullPath.size() - 1);
                    Object parent = entities.get(parentPath);

                    String path = segment.getKey();
                    String fieldNameInParent = getFieldNameInParent(path);
                    boolean isList = isPathList(path);

                    Object entity = createEntity(
                            parent,
                            fieldNameInParent,
                            isList,
                            resultSet,
                            columns,
                            segment.getValue().getFirstColumn() + 1,
                            segment.getValue().getLastColumn());

                    entities.put(fullPath, entity);

                    if (fullPath.size() == 1) {
                        topLevelRows.add(entity);
                    }
                }
            }
        }

        return topLevelRows;
    }

    private Object createEntity(Object parent,
                                String fieldNameInParent,
                                boolean isList,
                                ResultSet resultSet,
                                Map<Integer, ResultSetColumn> columns,
                                int firstColumn,
                                int lastColumn) {

        Object entity = this.entityCreator.createEntity(parent, fieldNameInParent, isList);
        for (int i = firstColumn; i <= lastColumn; i++) {
            ResultSetColumn column = columns.get(i);
            try {
                String label = column.getName();
                Object val = column.getConverter().getValueFromResultSet(lSql, resultSet, i);
                this.entityCreator.setValue(entity, label, val);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return entity;
    }

    private List<MarkerColumnValue> getFullPath(Map.Entry<String, SegmentHeader> segment,
                                                TreeMap<String, Object> markerColumnValues) {

        List<MarkerColumnValue> fullPath = newLinkedList();
        String path = segment.getKey();
        for (Map.Entry<String, Object> entry : markerColumnValues.entrySet()) {
            if (path.startsWith(entry.getKey())) {
                fullPath.add(new MarkerColumnValue(entry.getKey(), entry.getValue()));
            }
        }
        return fullPath;
    }

    private boolean isColumnMarker(String rawLabel) {
        rawLabel = rawLabel.trim();
        return rawLabel.startsWith("/") || rawLabel.startsWith("=");
    }

    private boolean isPathList(String path) {
        return path.trim().startsWith("/");
    }

    private String getFieldNameInParent(String path) {
        return path.substring(path.lastIndexOf("/") + 1).trim();
    }

}
