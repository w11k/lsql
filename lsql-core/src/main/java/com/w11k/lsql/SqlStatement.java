package com.w11k.lsql;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.DatabaseAccessException;
import com.w11k.lsql.exceptions.QueryException;
import com.w11k.lsql.jdbc.ConnectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlStatement {

    class Parameter {
        public String placeholder;

        String name;

        int startIndex;

        int endIndex;

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                    .append("name", name)
                    .append("startIndex", startIndex)
                    .append("endIndex", endIndex)
                    .toString();
        }
    }

    class ParameterInPreparedStatement {
        Parameter parameter;

        Object value;

        @Override
        public String toString() {
            return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
                    .append("name", parameter.name)
                    .append("value type", value.getClass().getCanonicalName())
                    .append("value", value)
                    .toString();
        }
    }


    // ..... /*name=*/ 123 /**/
    private static final Pattern QUERY_ARG_START = Pattern.compile(
            "/\\*\\s*(\\S*)\\s*=\\s*\\*/");

    private static final String QUERY_ARG_END = "/**/";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LSql lSql;

    private final String statementName;

    private final String sqlString;

    private final Map<String, List<Parameter>> parameters;

    private Map<String, Converter> inConverters = Maps.newLinkedHashMap();

    private Map<String, Converter> outConverters = Maps.newLinkedHashMap();

    public SqlStatement(LSql lSql, String statementName, String sqlString) {
        this.lSql = lSql;
        this.statementName = statementName;
        this.sqlString = sqlString;
        parameters = parseParameters();
    }

    public String getSqlString() {
        return sqlString;
    }

    public SqlStatement addInConverter(String parameterName, Converter converter) {
        this.inConverters.put(parameterName, converter);
        return this;
    }

    public SqlStatement setInConverters(Map<String, Converter> inConverters) {
        this.inConverters = inConverters;
        return this;
    }

    public SqlStatement addOutConverter(String columnName, Converter converter) {
        this.outConverters.put(columnName, converter);
        return this;
    }

    public SqlStatement setOutConverters(Map<String, Converter> outConverters) {
        this.outConverters = outConverters;
        return this;
    }

    public SqlStatement setInAndOutConverters(Map<String, Converter> inAndOutConverters) {
        this.inConverters = inAndOutConverters;
        this.outConverters = inAndOutConverters;
        return this;
    }

    public Query query() {
        return query(Maps.<String, Object>newHashMap());
    }

    public Query query(Object... keyVals) {
        return query(Row.fromKeyVals(keyVals));
    }

    public Query query(Map<String, Object> queryParameters) {
        try {
            PreparedStatement ps = createPreparedStatement(queryParameters);
            Query query = new Query(lSql, ps);
            query.setConverters(this.outConverters);
            return query;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void execute() {
        execute(Maps.<String, Object>newHashMap());
    }

    public void execute(Object... keyVals) {
        execute(Row.fromKeyVals(keyVals));
    }

    public void execute(Map<String, Object> queryParameters) {
        try {
            PreparedStatement ps = createPreparedStatement(queryParameters);
            ps.execute();
        } catch (SQLException e) {
            throw new DatabaseAccessException(e);
        }
    }

    private Map<String, List<Parameter>> parseParameters() {
        Map<String, List<Parameter>> found = Maps.newHashMap();

        Matcher matcher = QUERY_ARG_START.matcher(sqlString);

        while (matcher.find()) {
            Parameter p = new Parameter();

            // Name
            String paramName = matcher.group(1);
            paramName = paramName.equals("") ? extractParameterName(sqlString, matcher.start()) : paramName;
            p.name = paramName;

            // Start
            p.startIndex = matcher.start();

            // End
            int paramEnd = sqlString.indexOf(QUERY_ARG_END, p.startIndex);
            if (paramEnd == -1) {
                throw new IllegalArgumentException("Unable to find end marker for parameter '" + p.name + "'");
            }
            paramEnd += QUERY_ARG_END.length();
            p.endIndex = paramEnd;

            // Placeholder for PreparedStatement
            p.placeholder = "?" + StringUtils.repeat(" ", p.endIndex - p.startIndex - 1);
            List<Parameter> parametersForName = found.getOrDefault(p.name, Lists.<Parameter>newLinkedList());
            parametersForName.add(p);
            found.put(p.name, parametersForName);
        }
        return found;
    }

    private String extractParameterName(String sqlString, int start) {
        String left = sqlString.substring(0, start);
        left = left.trim();
        String[] leftTokens = StringUtils.split(left, "!=<> ");
        return leftTokens[leftTokens.length - 1];
     }

    private PreparedStatement createPreparedStatement(Map<String, Object> queryParameters) throws SQLException {
        logger.debug("Executing statement '{}' with parameters {}", statementName, queryParameters.keySet());

        List<ParameterInPreparedStatement> parameterInPreparedStatements = Lists.newLinkedList();
        String sqlStringCopy = sqlString;
        for (String queryParameter : queryParameters.keySet()) {
            List<Parameter> parametersByName = parameters.get(queryParameter);
            if (parametersByName == null) {
                throw new QueryException("Unused query parameter: " + queryParameter);
            }

            for (Parameter p : parametersByName) {
                String left = sqlStringCopy.substring(0, p.startIndex);
                String right = sqlStringCopy.substring(p.endIndex);
                // TODO 'remove line'-feature
                // TODO 'raw string replace'-feature
                sqlStringCopy = left + p.placeholder + right;

                ParameterInPreparedStatement pips = new ParameterInPreparedStatement();
                pips.parameter = p;
                pips.value = queryParameters.get(queryParameter);
                parameterInPreparedStatements.add(pips);
            }

        }

        // sort parameters by their position in the SQL statement
        parameterInPreparedStatements.sort(new Comparator<ParameterInPreparedStatement>() {
            public int compare(ParameterInPreparedStatement o1, ParameterInPreparedStatement o2) {
                return o1.parameter.startIndex - o2.parameter.startIndex;
            }
        });

        PreparedStatement ps = ConnectionUtils.prepareStatement(lSql, sqlStringCopy, false);
        for (int i = 0; i < parameterInPreparedStatements.size(); i++) {
            ParameterInPreparedStatement pips = parameterInPreparedStatements.get(i);

            if (pips.value instanceof QueryParameter) {
                QueryParameter queryParameter = (QueryParameter) pips.value;
                queryParameter.set(ps, i + 1);
            } else {
                Converter converter = this.inConverters.get(pips.parameter.name);

                if (converter == null) {
                    converter = lSql.getDialect().getConverterRegistry().getConverterForJavaValue(pips.value);
                }
                if (converter == null) {
                    throw new IllegalArgumentException(this.statementName + ": no registered converter for parameter " + pips);
                }
                converter.setValueInStatement(lSql, ps, i + 1, pips.value, converter.getSqlTypeForNullValues());
            }

        }


        return ps;


//        List<Parameter> found = checkQueryParameters(queryParameters);
//        sortCollectedParameters(found);
//        String sql = createSqlStringWithPlaceholders(queryParameters, found);


        // Set values
//        PreparedStatement ps = ConnectionUtils.prepareStatement(lSql, sql, false);
//        for (int i = 0; i < found.size(); i++) {
//            Parameter p = found.get(i);
//            if (queryParameters.containsKey(p.name)) {
//                Object value = queryParameters.get(p.name);
//                try {
//                    if (value != null) {
//                        if (value instanceof QueryParameter) {
//                            ((QueryParameter) value).set(ps, i + 1);
//                        } else {
//                            Converter converter = getConverterFor(p.name, value);
//                            converter.setValueInStatement(lSql, ps, i + 1, value, converter.getSqlTypeForNullValues());
//                        }
//                    } else {
//                        ps.setObject(i + 1, null);
//                    }
//                } catch (Exception e) {
//                    throw new QueryException(e);
//                }
//            }
//        }

        // Check for unused parameters
//        for (Parameter parameter : found) {
//            queryParameters.remove(parameter.name);
//        }
//        if (queryParameters.size() > 0) {
//            throw new QueryException("Unused query parameters: " + queryParameters.keySet());
//        }
//        return ps;
    }












    /*
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
    */

    /*


//    private void checkForEqualOperator(String line, Parameter p, int previousLinesLength, int valueStartInLine) {
//        String lineBeforeValue = line.substring(0, valueStartInLine);
//        Matcher matcher = EQUAL_OPERATOR_QUERY.matcher(lineBeforeValue);
//        if (matcher.find()) {
//            p.usesEqualOperator = true;
//            p.operatorStart = previousLinesLength + matcher.start(1);
//            p.operatorEnd = previousLinesLength + matcher.end(1);
//        }
//    }

//    private String queryParameterInLine(Map<String, Object> queryParameters, String line) {
//        for (String s : queryParameters.keySet()) {
//            if (line.startsWith(s + " ")
//                    || line.contains(" " + s + " ")
//                    || line.contains("." + s + " ")
//                    || line.contains("?" + s + " ")
//                    || line.contains("?" + s + "(")
//                    ) {
//                return s;
//            }
//        }
//        return null;
//    }

    private void sortCollectedParameters(List<Parameter> parameters) {
        Collections.sort(parameters, new Comparator<Parameter>() {
            @Override
            public int compare(Parameter o1, Parameter o2) {
                return new Integer(o1.indexStart).compareTo(o2.indexStart);
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
                    sql.append(sqlString.substring(lastIndex, p.indexStart));
                    sql.append("?");
                    lastIndex = p.valueEnd;
                }
            }
        }
        sql.append(sqlString.substring(lastIndex, sqlString.length()));
        return sql.toString();
    }

    private Converter getConverterFor(String paramName, Object value) {
//        String[] split = paramName.split("\\.");
//        if (split.length == 1) {
        // No table prefix
        //throw new RuntimeException("You must use <table>.<column> for query parameters!");
        return lSql.getDialect().getConverterRegistry().getConverterForJavaValue(value);
//        } else if (split.length == 2) {
//             With table prefix
//            String tableName = split[0];
//            String columnName = split[1];
//            Table table = lSql.table(tableName);
//            if (!table.exists()) {
//                 table not found, table name must be an alias
//                tableName = getTableAliasFromSqlStatement(tableName).get();
//                table = lSql.table(tableName);
//            }
//            return table.column(columnName).getConverter();
//        } else {
//            throw new RuntimeException("Invalid query parameter: " + paramName);
//        }
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

*/
}
