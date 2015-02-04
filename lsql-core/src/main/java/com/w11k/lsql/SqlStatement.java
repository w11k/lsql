package com.w11k.lsql;

import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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

import static com.google.common.base.Optional.absent;
import static com.google.common.base.Optional.of;

public class SqlStatement {

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
            "^.*(/\\*(\\?.+)?\\(\\*/.*/\\*\\)\\*/).*$");

    // column =
    private static final Pattern EQUAL_OPERATOR_QUERY = Pattern.compile(
            "^.*(=)\\s"
    );

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LSql lSql;

    private final String statementName;

    private final String sqlString;

    public SqlStatement(LSql lSql, String statementName, String sqlString) {
        this.lSql = lSql;
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
        return new Query(lSql, ps, this);
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
        logger.debug("Executing query '{}' with parameters {}", statementName, queryParameters.keySet());

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
                        if (value instanceof QueryParameter) {
                            ((QueryParameter) value).set(ps, i + 1);
                        } else {
                            Converter converter = getConverterFor(p.name, value);
                            converter.setValueInStatement(
                                    lSql, ps, i + 1, value, converter.getSqlTypeForNullValues());
                        }
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
            if (line.startsWith(s + " ") || line.contains(" " + s + " ") || line.contains("." + s + " ") || line.contains("?" + s + "")) {
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
            //throw new RuntimeException("You must use <table>.<column> for query parameters!");
            return lSql.getDialect().getConverterRegistry().getConverterForJavaValue(value);
        } else if (split.length == 2) {
            // With table prefix
            String tableName = split[0];
            String columnName = split[1];
            Table table = lSql.table(tableName);
            if (!table.exists()) {
                // table not found, table name must be an alias
                tableName = getTableAliasFromSqlStatement(tableName).get();
                table = lSql.table(tableName);
            }
            return table.column(columnName).getConverter();
        } else {
            throw new RuntimeException("Invalid query parameter: " + paramName);
        }
    }

    private Optional<String> getTableAliasFromSqlStatement(String usedAlias) {
        Pattern tableAlias = Pattern.compile(
                ".*[\n ,]+(\\w+)[\n ]+" + usedAlias.trim() + "[\n ,]+.*",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
        );
        Matcher matcher = tableAlias.matcher(sqlString);
        if (matcher.find() && lSql.table(matcher.group(1)).exists()) {
            return of(matcher.group(1));
        } else {
            return absent();
        }
    }

    public Optional<? extends Column> getColumnFromSqlStatement(String columnAliasName) {
        Pattern columnAlias = Pattern.compile(
                //".*[\n ,]+([\\w+\\.?\\w*])[\n ]+as " + usedAlias.trim() + "[\n ,]+.*",
                "((\\w+)\\.?(\\w*)) +as +" + columnAliasName.trim() + "[\\n ,]+",
                Pattern.CASE_INSENSITIVE | Pattern.MULTILINE
        );
        Matcher matcher = columnAlias.matcher(sqlString);
        if (matcher.find()) {
            String table = lSql.getDialect().identifierSqlToJava(matcher.group(2));
            String column = lSql.getDialect().identifierSqlToJava(matcher.group(3));
            if (!lSql.table(table).exists()) {
                Optional<String> tableOptional = getTableAliasFromSqlStatement(table);
                if (!tableOptional.isPresent()) {
                    return absent();
                }
                table = tableOptional.get();
            }
            return of(lSql.table(table).column(column));
        } else {
            return absent();
        }
    }

}
