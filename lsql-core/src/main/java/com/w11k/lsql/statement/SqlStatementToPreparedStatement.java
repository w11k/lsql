package com.w11k.lsql.statement;

import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.w11k.lsql.LSql;
import com.w11k.lsql.LiteralQueryParameter;
import com.w11k.lsql.QueryParameter;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.QueryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlStatementToPreparedStatement {

    // ..... /*name=*/ 123 /**/
    private static final Pattern QUERY_ARG_START = Pattern.compile(
            "/\\*\\s*(\\S*)\\s*=\\s*\\*/");

    private static final String QUERY_ARG_END = "/**/";

    private final class Parameter {
        String placeholder;

        String name;

        int startIndex;

        int endIndex;

        public String getName() {
            return name;
        }

        public int getStartIndex() {
            return startIndex;
        }

        public int getEndIndex() {
            return endIndex;
        }

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

    private final class ParameterInPreparedStatement {
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

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LSql lSql;

    private final String statementName;

    private final String sqlString;

    private final Map<String, List<Parameter>> parameters;

//    private Map<String, Converter> inConverters = Maps.newLinkedHashMap();

//    private Map<String, Converter> outConverters = Maps.newLinkedHashMap();

    public SqlStatementToPreparedStatement(LSql lSql, String statementName, String sqlString) {
        this.lSql = lSql;
        this.statementName = statementName;
        this.sqlString = sqlString;
        this.parameters = parseParameters();
    }

    public com.w11k.lsql.LSql getlSql() {
        return this.lSql;
    }

    public String getSqlString() {
        return sqlString;
    }

    public ImmutableMap<String, List<Parameter>> getParameters() {
        return ImmutableMap.copyOf(this.parameters);
    }

//    public SqlStatement addInConverter(String parameterName, Converter converter) {
//        this.inConverters.put(parameterName, converter);
//        return this;
//    }
//
//    public SqlStatement setInConverters(Map<String, Converter> inConverters) {
//        this.inConverters = inConverters;
//        return this;
//    }
//
//    public SqlStatement addOutConverter(String columnName, Converter converter) {
//        this.outConverters.put(columnName, converter);
//        return this;
//    }
//
//    public SqlStatement setOutConverters(Map<String, Converter> outConverters) {
//        this.outConverters = outConverters;
//        return this;
//    }
//
//    public SqlStatement setInAndOutConverters(Map<String, Converter> inAndOutConverters) {
//        this.inConverters = inAndOutConverters;
//        this.outConverters = inAndOutConverters;
//        return this;
//    }

//    public RowQuery query() {
//        return query(Maps.<String, Object>newHashMap());
//    }
//
//    public RowQuery query(Object... keyVals) {
//        return query(Row.fromKeyVals(keyVals));
//    }
//
//    public RowQuery query(Map<String, Object> queryParameters) {
//        try {
//            PreparedStatement ps = createPreparedStatement(queryParameters);
//            RowQuery query = new RowQuery(lSql, ps);
////            query.setConverters(this.outConverters);
//            return query;
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
//
//    }
//
//    public void execute() {
//        execute(Maps.<String, Object>newHashMap());
//    }
//
//    public void execute(Object... keyVals) {
//        execute(Row.fromKeyVals(keyVals));
//    }
//
//    public void execute(Map<String, Object> queryParameters) {
//        try {
//            PreparedStatement ps = createPreparedStatement(queryParameters);
//            ps.execute();
//        } catch (SQLException e) {
//            throw new DatabaseAccessException(e);
//        }
//    }

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

        Iterable<String> splitIter = Splitter.on(CharMatcher.anyOf("!=<> ")).omitEmptyStrings().split(left);
        return Iterables.getLast(splitIter);
    }

    private void log(Map<String, Object> queryParameters) {
        if (this.logger.isTraceEnabled()) {
            ArrayList<String> keys = Lists.newArrayList(queryParameters.keySet());
            Collections.sort(keys, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareToIgnoreCase(o2);
                }
            });
            String msg = "Executing statement '" + this.statementName + "' with parameters:\n";
            for (String key : keys) {
                msg += String.format("%15s = %s\n", key, queryParameters.get(key));
            }
            this.logger.trace(msg);
        } else if (this.logger.isDebugEnabled()) {
            this.logger.debug("Executing statement '{}' with parameters {}", this.statementName, queryParameters.keySet());
        }
    }

    private String processRawConversions(String sql, List<ParameterInPreparedStatement> parameterInPreparedStatements) {
        int lastIndex = 0;
        String sqlCopy = "";

        // Separate iteration because the following iteration will destroy the indexes
        for (ParameterInPreparedStatement pips : parameterInPreparedStatements) {
            if (pips.value instanceof LiteralQueryParameter) {
                LiteralQueryParameter literalQueryParameter = (LiteralQueryParameter) pips.value;
                sqlCopy += sql.substring(lastIndex, pips.parameter.startIndex);
                sqlCopy += literalQueryParameter.getSqlString();
                lastIndex = pips.parameter.endIndex;
            }
        }
        sqlCopy += sql.substring(lastIndex);

        return sqlCopy;
    }

    PreparedStatement createPreparedStatement(Map<String, Object> queryParameters) throws SQLException {
        log(queryParameters);

        List<ParameterInPreparedStatement> parameterInPreparedStatements = Lists.newLinkedList();
        String sqlStringCopy = this.sqlString;
        for (String queryParameter : queryParameters.keySet()) {
            List<Parameter> parametersByName = this.parameters.get(queryParameter);
            if (parametersByName == null) {
                throw new QueryException("Unused query parameter: " + queryParameter);
            }

            for (Parameter p : parametersByName) {
                String left = sqlStringCopy.substring(0, p.startIndex);
                String right = sqlStringCopy.substring(p.endIndex);
                sqlStringCopy = left + p.placeholder + right;

                ParameterInPreparedStatement pips = new ParameterInPreparedStatement();
                pips.parameter = p;
                pips.value = queryParameters.get(queryParameter);
                parameterInPreparedStatements.add(pips);
            }
        }

        // sort parameters by their position in the SQL statement
        Collections.sort(parameterInPreparedStatements, new Comparator<ParameterInPreparedStatement>() {
            public int compare(ParameterInPreparedStatement o1, ParameterInPreparedStatement o2) {
                return o1.parameter.startIndex - o2.parameter.startIndex;
            }
        });

        // RAW conversions
        sqlStringCopy = processRawConversions(sqlStringCopy, parameterInPreparedStatements);

        PreparedStatement ps = this.lSql.getDialect().getStatementCreator().createPreparedStatement(this.lSql, sqlStringCopy, false);

        int offset = 0;
        for (int i = 0; i < parameterInPreparedStatements.size(); i++) {
            ParameterInPreparedStatement pips = parameterInPreparedStatements.get(i);

            if (pips.value instanceof QueryParameter) {
                QueryParameter queryParameter = (QueryParameter) pips.value;
                queryParameter.set(ps, i + 1);
            } else if (pips.value instanceof LiteralQueryParameter) {
                LiteralQueryParameter dqp = (LiteralQueryParameter) pips.value;
                for (int localIndex = 0; localIndex < dqp.getNumberOfQueryParameters(); localIndex++) {
                    dqp.set(ps, i + 1 + offset + localIndex, localIndex);
                }

                // -1 because one ? was already set
                offset += dqp.getNumberOfQueryParameters() - 1;
            } else {
                Converter converter = this.lSql.getDialect().getConverterRegistry()
                        .getConverterForJavaType(pips.value.getClass());

                if (converter == null) {
                    throw new IllegalArgumentException(this.statementName + ": no registered converter for parameter " + pips);
                }
                converter.setValueInStatement(this.lSql, ps, i + offset + 1, pips.value);
            }
        }

        return ps;
    }

}
