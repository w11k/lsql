package com.w11k.lsql;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
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

public class SqlStatement {

    static class RawConverter {
    }

    static final class RAW_REMOVE_LINE extends RawConverter {
        private RAW_REMOVE_LINE() {
        }
    }

    public static final RAW_REMOVE_LINE RAW_REMOVE_LINE = new RAW_REMOVE_LINE();

    class Parameter {
        public String placeholder;

        String name;

        int startIndex;

        int endIndex;

        @Override
        public String toString() {
            return "Parameter{" +
              "placeholder='" + placeholder + '\'' +
              ", name='" + name + '\'' +
              ", startIndex=" + startIndex +
              ", endIndex=" + endIndex +
              '}';
        }
    }

    class ParameterInPreparedStatement {
        Parameter parameter;

        Object value;

        @Override
        public String toString() {
            return "ParameterInPreparedStatement{" +
              "parameter=" + parameter +
              ", value=" + value +
              '}';
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
//            p.placeholder = "?" + StringUtils.repeat(" ", p.endIndex - p.startIndex - 1);
            p.placeholder = "?" + Strings.repeat(" ", p.endIndex - p.startIndex - 1);


            List<Parameter> parametersForName = found.containsKey(p.name) ? found.get(p.name) : Lists.<Parameter>newLinkedList();


            parametersForName.add(p);
            found.put(p.name, parametersForName);
        }
        return found;
    }

    private String extractParameterName(String sqlString, int start) {
        String left = sqlString.substring(0, start);
        left = left.trim();

//        String[] leftTokens = StringUtils.split(left, "!=<> ");
//        String apache = leftTokens[leftTokens.length - 1];

        Iterable<String> splitIter = Splitter.on(CharMatcher.anyOf("!=<> ")).omitEmptyStrings().split(left);
        return Iterables.getLast(splitIter);
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
                // TODO 'raw string replace'-feature
                sqlStringCopy = left + p.placeholder + right;

                ParameterInPreparedStatement pips = new ParameterInPreparedStatement();
                pips.parameter = p;
                pips.value = queryParameters.get(queryParameter);
                parameterInPreparedStatements.add(pips);
            }
        }

        // RAW conversions
        sqlStringCopy = processRawConversions(sqlStringCopy, parameterInPreparedStatements);

        // sort parameters by their position in the SQL statement
        Collections.sort(parameterInPreparedStatements, new Comparator<ParameterInPreparedStatement>() {
            public int compare(ParameterInPreparedStatement o1, ParameterInPreparedStatement o2) {
                return o1.parameter.startIndex - o2.parameter.startIndex;
            }
        });

        PreparedStatement ps = ConnectionUtils.prepareStatement(lSql, sqlStringCopy, false);
        for (int i = 0; i < parameterInPreparedStatements.size(); i++) {
            ParameterInPreparedStatement pips = parameterInPreparedStatements.get(i);

            // Skip raw conversions
            if (pips.value instanceof RawConverter) {
                continue;
            }

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
    }

    private String processRawConversions(String sql, List<ParameterInPreparedStatement> parameterInPreparedStatements) {
        for (ParameterInPreparedStatement pips : parameterInPreparedStatements) {
            if (pips.value.equals(RAW_REMOVE_LINE)) {

                int startIndex = pips.parameter.startIndex;
//                int beginLine = StringUtils.lastIndexOf(sql.substring(0, startIndex), "\n");
                int beginLine = sql.substring(0, startIndex).lastIndexOf("\n");

                int endIndex = pips.parameter.endIndex;
//                int endLine = StringUtils.indexOf(sql, "\n", endIndex);
                int endLine = sql.indexOf("\n", endIndex);

                sql = sql.substring(0, beginLine);
//                sql += StringUtils.repeat(" ", endLine - beginLine);
                sql += Strings.repeat(" ", endLine - beginLine);
                sql += sql.substring(endLine);
            }
        }
        return sql;
    }

}
