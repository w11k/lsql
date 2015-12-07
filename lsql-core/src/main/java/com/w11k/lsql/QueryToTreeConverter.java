package com.w11k.lsql;

import com.google.common.base.Optional;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.w11k.lsql.converter.Converter;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

class QueryToTreeConverter {

    private final Query query;
    private final ResultSet resultSet;
    private final ResultSetMetaData metaData;
    private Map<Integer, ResultSetColumn> resultSetColumns = Maps.newHashMap();
    private Pattern markerFirstColumn = Pattern.compile("(.+)/(:.+)?");
    /**
     * group 1: value
     * group 2: field
     * group 3: table
     */
    private Pattern markerColumn = Pattern.compile("(.+?)/(.+)(:.+)?");
    private LinkedHashMap<String, Row> tree;

    public QueryToTreeConverter(Query query) {
        this.query = query;
        try {
            this.resultSet = query.getPreparedStatement().executeQuery();
            this.metaData = resultSet.getMetaData();
            createTree();
            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Optional<ResultSetColumn> getResultSetColumn(int columnNumber) throws SQLException {
        if (resultSetColumns.containsKey(columnNumber)) {
            return of(resultSetColumns.get(columnNumber));
        }

        String columnValue = resultSet.getString(columnNumber);
        if (columnValue == null) {
            return absent();
        }

        // Marker Column?
        Pattern pattern = columnNumber == 1 ? markerFirstColumn : markerColumn;
        Matcher matcher = pattern.matcher(columnValue);
        if (matcher.find()) {
            ResultSetColumn rsc = new ResultSetColumn(columnNumber, "marker column " + columnNumber, pattern);
            resultSetColumns.put(columnNumber, rsc);
            return of(rsc);
        }

        String columnLabel = query.getlSql().getDialect().identifierSqlToJava(metaData.getColumnLabel(columnNumber));

        // Registered Converter
        Converter converter = query.getConverterForResultSetColumn(metaData, columnNumber, columnLabel);
        ResultSetColumn rsc = new ResultSetColumn(columnNumber, columnLabel, converter);
        resultSetColumns.put(columnNumber, rsc);
        return of(rsc);


    }

    private void createTree() throws SQLException {
        LinkedHashMap<String, Row> tree = Maps.newLinkedHashMap();

        while (resultSet.next()) {
            List<MarkerColumnValue> markers = Lists.newLinkedList();
            Row row = null;

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                Optional<ResultSetColumn> rscOpt = getResultSetColumn(i);
                if (!rscOpt.isPresent()) {
                    continue;
                }

                ResultSetColumn rsc = rscOpt.get();

                if (rsc.isMarkerColumn()) {
                    Optional<MarkerColumnValue> markerOpt = getMarkerColumnValue(i, rsc);
                    if (markerOpt.isPresent()) {
                        markers.add(markerOpt.get());
                        row = getTargetRow(tree, markers);
                    } else {
                        row = null;
                    }
                }

                if (!rsc.isMarkerColumn()) {
                    if (row != null) {
                        row.put(rsc.getName(), rsc.getConverter().getValueFromResultSet(query.getlSql(), resultSet, i));
                    }
                }
            }

        }

        this.tree = tree;
    }

    private Row getTargetRow(LinkedHashMap<String, Row> tree, List<MarkerColumnValue> markers) {
        MarkerColumnValue lastMarkerColumnValue = markers.get(markers.size() - 1);
        List<MarkerColumnValue> path = getMarkerPathTo(markers, lastMarkerColumnValue);

        Row row = null;
        for (MarkerColumnValue marker : path) {
            if (!marker.getField().equals("")) {
                //noinspection ConstantConditions Never happens, Root Marker will trigger first
                if (row.get(marker.getField()) == null) {
                    row.put(marker.getField(), Maps.newLinkedHashMap());
                }
                tree = row.getTree(marker.getField());
            }

            if (tree.get(marker.getId()) == null) {
                tree.put(marker.getId(), new Row());
            }
            row = tree.get(marker.getId());
        }

        return row;
    }

    private List<MarkerColumnValue> getMarkerPathTo(List<MarkerColumnValue> markers, MarkerColumnValue target) {
        int lastLevel = 0;
        List<MarkerColumnValue> path = Lists.newLinkedList();

        for (MarkerColumnValue marker : markers) {
            if (marker.getLevel() == lastLevel + 1 && target.getPath().startsWith(marker.getPath())) {
                path.add(marker);
                lastLevel = marker.getLevel();
            }
        }

        return path;
    }

    private Optional<MarkerColumnValue> getMarkerColumnValue(int i, ResultSetColumn rsc) throws SQLException {
        String markerValue = resultSet.getString(i);
        if (markerValue == null) {
            return Optional.absent();
        }
        Matcher matcher = rsc.getPattern().matcher(markerValue);
        if (!matcher.find()) {
            // should never happen
            throw new IllegalStateException();
        }

        String id = matcher.group(1);
        if (i == 1) {
            return of(new MarkerColumnValue(id, "", 1, matcher.group(2)));
        } else {
            String path = matcher.group(2);
            return of(new MarkerColumnValue(id, path, Iterables.size(Splitter.on("/").split(path)) + 1, matcher.group(3)));
        }
    }

    public LinkedHashMap<String, Row> getTree() {
        return tree;
    }

    private static class MarkerColumnValue {

        private final int level;
        private String id;
        private String path;
        private String field;
        private String table;

        public MarkerColumnValue(String id, String path, int level, String table) {
            this.id = id;
            this.path = path;
            int lastSlash = path.lastIndexOf('/') + 1;
            this.field = path.substring(lastSlash);
            this.level = level;
            this.table = table;
        }

        public String getId() {
            return id;
        }

        public String getPath() {
            return path;
        }

        public String getField() {
            return field;
        }

        public String getTable() {
            return table;
        }

        public int getLevel() {
            return level;
        }
    }

}
