package com.w11k.lsql.sqlfile;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.w11k.lsql.LSql;
import com.w11k.lsql.Query;
import com.w11k.lsql.Row;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.DatabaseAccessException;
import com.w11k.lsql.exceptions.QueryException;
import com.w11k.lsql.jdbc.ConnectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LSqlFileStatement {

    class Parameter {
        String name;

        int valueStart;

        int valueEnd;

        boolean usesEqualOperator = false;

        int operatorStart;

        int operatorEnd;
    }

    // column = /*(*/ 123 /*)*/
    private static final Pattern RANGE_QUERY_ARG = Pattern.compile(
            "^.*(/\\*\\(\\*/.*/\\*\\)\\*/).*$");

    // column =
    private static final Pattern EQUAL_OPERATOR_QUERY = Pattern.compile(
            "^.*(=)\\s"
    );

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LSql lSql;

    private final LSqlFile lSqlFile;

    private final String statementName;

    private final String sqlString;

    LSqlFileStatement(LSql lSql, LSqlFile lSqlFile, String statementName, String sqlString) {
        this.lSql = lSql;
        this.lSqlFile = lSqlFile;
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
        return query(Row.fromKeyVals(keyVals));
    }

    public Query query(Map<String, Object> queryParameters) {
        PreparedStatement ps = createPreparedStatement(queryParameters);
        return new Query(lSql, ps);
    }

    public void execute() {
        execute(Maps.<String, Object>newHashMap());
    }

    public void execute(Object... keyVals) {
        execute(Row.fromKeyVals(keyVals));
    }

    public void execute(Map<String, Object> queryParameters) {
        PreparedStatement ps = createPreparedStatement(queryParameters);
        try {
            ps.execute();
        } catch (SQLException e) {
            throw new DatabaseAccessException(e);
        }
    }

    private PreparedStatement createPreparedStatement(Map<String, Object> queryParameters) {
        logger.debug("Executing query '{}' ({}) with parameters {}",
                statementName, lSqlFile.getFileName(), queryParameters.keySet());

        List<Parameter> found = checkQueryParameters(queryParameters);
        sortCollectedParameters(found);
        String sql = createSqlStringWithPlaceholders(queryParameters, found);

        if (logger.isTraceEnabled()) {
            List<String> keyVals = Lists.newLinkedList();
            for (String key : queryParameters.keySet()) {
                keyVals.add(key + ": " + queryParameters.get(key));
            }
            logger.trace("\n" +
                    "------------------------------------------------------------\n" +
                    "Executing '{}'\n" +
                    "------------------------------------------------------------\n" +
                    Joiner.on("\n").join(keyVals) + "\n" +
                    "------------------------------------------------------------\n" +
                    "{}\n" +
                    "------------------------------------------------------------\n" +
                    "\n",
                    statementName, sql);
        }

        // Set values
        PreparedStatement ps = ConnectionUtils.prepareStatement(lSql, sql, false);
        for (int i = 0; i < found.size(); i++) {
            Parameter p = found.get(i);
            if (queryParameters.containsKey(p.name)) {
                Object value = queryParameters.get(p.name);
                try {
                    if (value != null) {
                        Converter converter = getConverterFor(p.name, value);
                        converter.setValueInStatement(lSql, ps, i + 1, value, converter.getSqlTypeForNullValues());
                    } else {
                        ps.setObject(i + 1, null);
                    }
                } catch (Exception e) {
                    throw new QueryException(e);
                }
            }
        }

        // Check for unused parameters
        for (Parameter parameter : found) {
            queryParameters.remove(parameter.name);
        }
        if (queryParameters.size() > 0) {
            throw new QueryException("Unused query parameters: " + queryParameters.keySet());
        }
        return ps;
    }

    private List<Parameter> checkQueryParameters(Map<String, Object> queryParameters) {
        List<Parameter> found = Lists.newLinkedList();

        int previousLinesLength = 0;
        String[] lines = sqlString.split("\n");
        for (String line : lines) {
            String paramName;
            if ((paramName = queryParameterInLine(queryParameters, line)) != null) {
                Matcher matcher = RANGE_QUERY_ARG.matcher(line);
                if (matcher.find()) {
                    Parameter p = new Parameter();
                    p.name = paramName;
                    p.valueStart = previousLinesLength + matcher.start(1);
                    p.valueEnd = previousLinesLength + matcher.end(1);
                    checkForEqualOperator(line, p, previousLinesLength, matcher.start(1));
                    found.add(p);
                }
            }
            previousLinesLength += (line + "\n").length();
        }

        return found;
    }

    private void checkForEqualOperator(String line, Parameter p, int previousLinesLength, int valueStartInLine) {
        String lineBeforeValue = line.substring(0, valueStartInLine);
        Matcher matcher = EQUAL_OPERATOR_QUERY.matcher(lineBeforeValue);
        if (matcher.find()) {
            p.usesEqualOperator = true;
            p.operatorStart = previousLinesLength + matcher.start(1);
            p.operatorEnd = previousLinesLength + matcher.end(1);
        }
    }

    private String queryParameterInLine(Map<String, Object> queryParameters, String line) {
        for (String s : queryParameters.keySet()) {
            if (line.contains(" " + s + " ")) {
                return s;
            }
        }
        return null;
    }

    private void sortCollectedParameters(List<Parameter> parameters) {
        Collections.sort(parameters, new Comparator<Parameter>() {
            @Override
            public int compare(Parameter o1, Parameter o2) {
                return new Integer(o1.valueStart).compareTo(o2.valueStart);
            }
        });
    }

    private String createSqlStringWithPlaceholders(Map<String, Object> queryParameters,
                                                   List<Parameter> parameters) {
        int lastIndex = 0;
        StringBuilder sql = new StringBuilder();
        for (Parameter p : parameters) {
            if (queryParameters.containsKey(p.name)) {
                if (p.usesEqualOperator && queryParameters.get(p.name) == null) {
                    sql.append(sqlString.substring(lastIndex, p.operatorStart));
                    sql.append("is null");
                    lastIndex = p.valueEnd;
                    queryParameters.remove(p.name);
                } else {
                    sql.append(sqlString.substring(lastIndex, p.valueStart));
                    sql.append("?");
                    lastIndex = p.valueEnd;
                }
            }
        }
        sql.append(sqlString.substring(lastIndex, sqlString.length()));
        return sql.toString();
    }

    private Converter getConverterFor(String paramName, Object value) {
        String[] split = paramName.split("\\.");
        if (split.length == 1) {
            // No table prefix
            return lSql.getDialect().getConverterRegistry().getConverterForJavaValue(value);
        } else if (split.length == 2) {
            // With table prefix
            String tableName = split[0];
            String columnName = split[1];
            return lSql.table(tableName).column(columnName).getConverter();
        } else {
            throw new RuntimeException("Invalid query parameter: " + paramName);
        }
    }

}
