package com.w11k.lsql.statement;

import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.w11k.lsql.LSql;
import com.w11k.lsql.ListLiteralQueryParameter;
import com.w11k.lsql.LiteralQueryParameter;
import com.w11k.lsql.QueryParameter;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.exceptions.QueryException;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.CharMatcher.anyOf;
import static com.google.common.collect.Lists.newLinkedList;

public class SqlStatementToPreparedStatement {

    // ..... /*name: type =*/ 123 /**/
    private static final Pattern QUERY_ARG_START = Pattern.compile(
            "/\\*\\s*(?!\\*/)(\\S*)\\s*:?\\s*(\\S*)\\s*=\\s*\\*/");

    private static final String QUERY_ARG_END = "/**/";

    private static final Pattern OUT_TYPE_ANNOTATION = Pattern.compile(
            "(/\\*\\s*:\\s*(\\w*)\\s*\\*/)",
            Pattern.MULTILINE
    );

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LSql lSql;

    private final String statementName;

    private final String typeAnnotation;

    private final String sqlString;

    private final Map<String, List<Parameter>> parameters;

    private final Map<String, Converter> outConverters;

    public SqlStatementToPreparedStatement(LSql lSql, String statementName, String typeAnnotation, String sqlString) {
        this.lSql = lSql;
        this.statementName = statementName.trim();
        this.typeAnnotation = typeAnnotation.trim();
        this.sqlString = sqlString;
        this.parameters = parseParameters();
        this.outConverters = parseOutConverters();
    }

    public com.w11k.lsql.LSql getlSql() {
        return this.lSql;
    }

    public String getStatementName() {
        return statementName;
    }

    public String getTypeAnnotation() {
        return typeAnnotation;
    }

    public String getSqlString() {
        return sqlString;
    }

    public ImmutableMap<String, List<Parameter>> getParameters() {
        return ImmutableMap.copyOf(this.parameters);
    }

    public Map<String, Converter> getOutConverters() {
        return outConverters;
    }

    private Map<String, List<Parameter>> parseParameters() {
        Map<String, List<Parameter>> found = Maps.newHashMap();

        Matcher matcher = QUERY_ARG_START.matcher(sqlString);

        while (matcher.find()) {
            Parameter p = new Parameter();

            // name and type
            String name = matcher.group(1).trim();
            String type = matcher.group(2).trim();

            if (name.equals(":")) {
                name = "";
            } else if (name.startsWith(":")) {
                type = name.substring(1);
                name = "";
            } else if (name.contains(":")) {
                String[] split = name.split(":");
                name = split[0];
                type = split[1];
            }

            name = name.equals("") ? extractParameterName(sqlString, matcher.start()) : name;
            p.name = name;
            p.javaTypeAlias = type;

            // Start
            p.startIndex = matcher.start();

            // End
            int paramEnd = sqlString.indexOf(QUERY_ARG_END, p.startIndex);
            if (paramEnd == -1) {
                throw new IllegalArgumentException("Unable to find end marker for parameter '" + p.name + "'");
            }

            if (Strings.isNullOrEmpty(p.javaTypeAlias)) {
                String placeHolderValue = sqlString.substring(matcher.end(0), paramEnd).trim();
                if (placeHolderValue.startsWith("'") && placeHolderValue.endsWith("'")) {
                    p.javaTypeAlias = "string";
                } else if (NumberUtils.isNumber(placeHolderValue)) {
                    p.javaTypeAlias = "int";
                }

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

        Iterable<String> splitIter = Splitter.on(anyOf("!=<> ")).omitEmptyStrings().split(left);
        ArrayList<String> strings = Lists.newArrayList(splitIter);
        String name = strings.get(strings.size() - 1);
        if (name.toUpperCase().equals("IS")) {
            name = strings.get(strings.size() - 2);
        }
        return name;
    }

    private Map<String, Converter> parseOutConverters() {
        Matcher matcher = OUT_TYPE_ANNOTATION.matcher(this.sqlString);

        Map<String, Converter> converters = Maps.newHashMap();
        while (matcher.find()) {
            String alias = matcher.group(2);
            String beforeAlias = this.sqlString.substring(0, matcher.start(1)).trim();

            LinkedList<String> strings = newLinkedList(Splitter.on(anyOf(" ,\t")).split(beforeAlias));
            String last = strings.getLast();

            String javaColumnName = this.getlSql().identifierSqlToJava(last);
            Converter converter = this.getlSql().getConverterForAlias(alias);
            converters.put(javaColumnName, converter);
        }

        return converters;
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

    public PreparedStatement createPreparedStatement(Map<String, Object> queryParameters,
                                                     Map<String, Converter> parameterConverters) throws SQLException {
        log(queryParameters);

        List<ParameterInPreparedStatement> parameterInPreparedStatements = newLinkedList();
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

                // converter specified by API
                if (parameterConverters != null) {
                    pips.converter = parameterConverters.get(pips.parameter.name);
                }

                // converter specified by statement's 'param: type' annotation
                if (pips.converter == null) {
                    String parameterTypeAlias = p.getJavaTypeAlias();
                    if (!Strings.isNullOrEmpty(parameterTypeAlias)) {
                        pips.converter = this.lSql.getConverterForAlias(parameterTypeAlias);
                    }
                }

                // check if the param type is correct
                if (pips.converter != null
                        && !(pips.value instanceof QueryParameter)
                        && !(pips.value instanceof ListLiteralQueryParameter)
                        && !pips.converter.isValueValid(pips.value)) {

                    throw new IllegalArgumentException("Value for parameter '" + p.name + "' has the wrong type. "
                            + "Expected: " + pips.converter.getJavaType().getCanonicalName()
                            + ", actual: " + pips.value.getClass().getName());
                }

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

        PreparedStatement ps = this.lSql.getStatementCreator()
                .createPreparedStatement(this.lSql, sqlStringCopy, false);

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
            } else if (pips.value == null) {
                ps.setNull(i + offset + 1, Types.OTHER);
            } else {
                // converter by param type
                if (pips.converter == null) {
                    pips.converter = this.lSql.getConverterForJavaType(pips.value.getClass());
                }

                if (pips.converter == null) {
                    throw new IllegalArgumentException(this.statementName + ": no registered converter for parameter " + pips);
                }
                pips.converter.setValueInStatement(this.lSql, ps, i + offset + 1, pips.value);
            }
        }

        return ps;
    }

    public static final class Parameter {
        String placeholder;

        String name;

        String javaTypeAlias;

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

        public String getJavaTypeAlias() {
            return javaTypeAlias;
        }

        @Override
        public String toString() {
            return "Parameter{" +
                    "placeholder='" + placeholder + '\'' +
                    ", name='" + name + '\'' +
                    ", javaTypeAlias='" + javaTypeAlias + '\'' +
                    ", startIndex=" + startIndex +
                    ", endIndex=" + endIndex +
                    '}';
        }
    }

    private final class ParameterInPreparedStatement {

        Parameter parameter;

        Object value;

        Converter converter;

        @Override
        public String toString() {
            return "ParameterInPreparedStatement{" +
                    "parameter=" + parameter +
                    "converter=" + converter +
                    ", value=" + value +
                    '}';
        }
    }

}
