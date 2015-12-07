package com.w11k.lsql;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.w11k.lsql.converter.Converter;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Optional.of;

class QueryToTreeConverter {

    private static class MarkerColumnValue {

        private final ResultSetColumn resultSetColumn;
        private final Number id;

        public MarkerColumnValue(ResultSetColumn resultSetColumn, Number id) {
            this.resultSetColumn = resultSetColumn;
            this.id = id;
        }


        public ResultSetColumn getResultSetColumn() {
            return resultSetColumn;
        }

        public Number getId() {
            return id;
        }
    }

    private final Query query;

    private final ResultSet resultSet;

    private final ResultSetMetaData metaData;

    private String markerColumnPrefix = "/";

    private Map<Integer, ResultSetColumn> resultSetColumns = Maps.newHashMap();


    private LinkedHashMap<Number, Row> tree;

    public QueryToTreeConverter(Query query) {
        this.query = query;
        try {
            this.resultSet = query.getPreparedStatement().executeQuery();
            this.metaData = resultSet.getMetaData();

            createResultSetColumns();
            createTree();

            resultSet.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createResultSetColumns() throws SQLException {
        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String label = query.getlSql().getDialect().identifierSqlToJava(metaData.getColumnLabel(i));

            // Marker column?
            if (label.startsWith(markerColumnPrefix)) {
                String fullPath = label.substring(markerColumnPrefix.length());
                ResultSetColumn rsc = new ResultSetColumn(i, "marker column " + i + " [" + label + "]", fullPath);
                resultSetColumns.put(i, rsc);
                continue;
            }

            // Registered Converter
            Converter converter = query.getConverterForResultSetColumn(metaData, i, label);
            ResultSetColumn rsc = new ResultSetColumn(i, label, converter);
            resultSetColumns.put(i, rsc);
        }
    }

    private void createTree() throws SQLException {
        LinkedHashMap<Number, Row> tree = Maps.newLinkedHashMap();

        while (resultSet.next()) {
            List<MarkerColumnValue> markers = Lists.newLinkedList();
            Row row = null;

            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                ResultSetColumn rsc = resultSetColumns.get(i);

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

    private Optional<MarkerColumnValue> getMarkerColumnValue(int i, ResultSetColumn rsc) throws SQLException {
        String markerValue = resultSet.getString(i);
        if (markerValue == null) {
            return Optional.absent();
        }

        String idString = resultSet.getString(rsc.getPosition());
        int id = Integer.parseInt(idString);
        return of(new MarkerColumnValue(rsc, id));
    }

    private Row getTargetRow(LinkedHashMap<Number, Row> tree, List<MarkerColumnValue> markers) {
        MarkerColumnValue lastMarkerColumnValue = markers.get(markers.size() - 1);
        List<MarkerColumnValue> path = getMarkerPathTo(markers, lastMarkerColumnValue);

        Row row = null;
        for (MarkerColumnValue marker : path) {
            if (!marker.getResultSetColumn().getField().equals("")) {
                //noinspection ConstantConditions Never happens, Root Marker will trigger first
                if (row.get(marker.getResultSetColumn().getField()) == null) {
                    row.put(marker.getResultSetColumn().getField(), Maps.newLinkedHashMap());
                }
                tree = row.getTree(marker.getResultSetColumn().getField());
            }

            if (tree.get(marker.getId()) == null) {
                tree.put(marker.getId(), new Row());
            }
            row = tree.get(marker.getId());
        }

        return row;
    }

    private List<MarkerColumnValue> getMarkerPathTo(List<MarkerColumnValue> markers, MarkerColumnValue target) {
        int lastLevel = -1;
        List<MarkerColumnValue> path = Lists.newLinkedList();

        for (MarkerColumnValue marker : markers) {
            if (marker.getResultSetColumn().getLevel() == lastLevel + 1
              && target.getResultSetColumn().getFullPath().startsWith(marker.getResultSetColumn().getFullPath())) {
                path.add(marker);
                lastLevel = marker.getResultSetColumn().getLevel();
            }
        }

        return path;
    }

    public LinkedHashMap<Number, Row> getTree() {
        return tree;
    }

}
