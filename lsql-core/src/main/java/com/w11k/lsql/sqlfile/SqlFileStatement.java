package com.w11k.lsql.sqlfile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.DatabaseAccessException;
import com.w11k.lsql.exceptions.QueryException;
import com.w11k.lsql.relational.Query;
import com.w11k.lsql.relational.Row;
import com.w11k.lsql.utils.ConnectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlFileStatement {

    class Parameter {
        String name;
        int valueStart;
        int valueEnd;
    }

    // column = 'value' --param
    private static final Pattern QUOTED_QUERY_ARG = Pattern.compile(
            "^.*[^\\\\]('.*')\\s*--\\s*([\\w\\.]+)\\s*$", Pattern.MULTILINE);
    // column = 123 --param
    private static final Pattern UNQUOTED_QUERY_ARG = Pattern.compile(
            "^.*\\s+(\\w+)\\s+--\\s*([\\w\\.]+)\\s*$", Pattern.MULTILINE);
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final LSql lSql;
    private final SqlFile sqlFile;
    private final String statementName;
    private final String sqlString;

    SqlFileStatement(LSql lSql, SqlFile sqlFile, String statementName, String sqlString) {
        this.lSql = lSql;
        this.sqlFile = sqlFile;
        this.statementName = statementName;
        this.sqlString = sqlString;
    }

    public String getSqlString() {
        return sqlString;
    }

    public Query query() {
        return query(Maps.<String, Object>newHashMap());
    }

    public Query query(Object... keyVals) {
        Row r = new Row();
        r.addKeyVals(keyVals);
        return query(r);
    }

    public Query query(Map<String, Object> queryParameters) {

        logger.debug("Executing query '{}' ({}) with parameters {}",
                statementName, sqlFile.getFileName(), queryParameters.keySet());

        List<Parameter> parameters = Lists.newLinkedList();
        checkEndOfLineNamedParameters(parameters);
        sortCollectedParameters(parameters);
        String sql = createSqlStringWithPlaceholders(queryParameters, parameters);

        // Set values
        PreparedStatement ps = ConnectionUtils.prepareStatement(lSql, sql, false);
        for (int i = 0; i < parameters.size(); i++) {
            Parameter p = parameters.get(i);
            if (queryParameters.containsKey(p.name)) {
                Converter converter = getConverterFor(p.name);
                try {
                    converter.setValueInStatement(ps, i + 1, queryParameters.get(p.name));
                } catch (Exception e) {
                    throw new QueryException(e);
                }
            }
        }

        // Check for unused parameters
        for (Parameter parameter : parameters) {
            queryParameters.remove(parameter.name);
        }
        if (queryParameters.size() > 0) {
            throw new QueryException("Unused query parameters: " + queryParameters.keySet());
        }

        return new Query(lSql, ps);
    }

    public void execute() {
        PreparedStatement ps = ConnectionUtils.prepareStatement(lSql, sqlString, false);
        try {
            ps.execute();
        } catch (SQLException e) {
            throw new DatabaseAccessException(e);
        }
    }

    private void checkEndOfLineNamedParameters(List<Parameter> parameters) {
        for (Pattern pattern : Arrays.asList(QUOTED_QUERY_ARG, UNQUOTED_QUERY_ARG)) {
            Matcher matcher = pattern.matcher(sqlString);
            while (matcher.find()) {
                String paramName = matcher.group(2);

                Parameter p = new Parameter();
                p.name = paramName;
                p.valueStart = matcher.start(1);
                p.valueEnd = matcher.end(1);

                parameters.add(p);
            }
        }
    }

    private void sortCollectedParameters(List<Parameter> parameters) {
        Collections.sort(parameters, new Comparator<Parameter>() {
            @Override public int compare(Parameter o1, Parameter o2) {
                return new Integer(o1.valueStart).compareTo(o2.valueStart);
            }
        });
    }

    private String createSqlStringWithPlaceholders(Map<String, Object> queryParameters, List<Parameter> parameters) {
        int lastIndex = 0;
        StringBuilder sql = new StringBuilder();
        for (Parameter p : parameters) {
            if (queryParameters.containsKey(p.name)) {
                sql.append(sqlString.substring(lastIndex, p.valueStart));
                sql.append("?");
                lastIndex = p.valueEnd;
            }
        }
        sql.append(sqlString.substring(lastIndex, sqlString.length()));
        return sql.toString();
    }

    private Converter getConverterFor(String paramName) {
        String[] split = paramName.split("\\.");
        if (split.length != 2) {
            return lSql.getGlobalConverter();
        }
        String tableName = split[0];
        String columnName = split[1];
        return lSql.table(tableName).column(columnName).getColumnConverter();
    }

}
