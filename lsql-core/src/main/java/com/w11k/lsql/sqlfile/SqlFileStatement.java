package com.w11k.lsql.sqlfile;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.JavaSqlConverter;
import com.w11k.lsql.exceptions.DatabaseAccessException;
import com.w11k.lsql.exceptions.QueryException;
import com.w11k.lsql.relational.Query;
import com.w11k.lsql.relational.Row;
import com.w11k.lsql.utils.ConnectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlFileStatement {

    private static final Pattern QUOTED_QUERY_ARG = Pattern.compile(
            "^.*[^\\\\]('.*')\\s*--\\s*(\\w*)\\s*$");

    private static final Pattern UNQUOTED_QUERY_ARG = Pattern.compile(
            "^.*\\s+(\\w+)\\s+--\\s*(\\w+)\\s*$");

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

    public void execute() {
        PreparedStatement ps = ConnectionUtils.prepareStatement(lSql, sqlString);
        try {
            ps.execute();
        } catch (SQLException e) {
            throw new DatabaseAccessException(e);
        }
    }

    public Query query() {
        return query(Maps.<String, Object>newHashMap());
    }

    public Query query(Object... keyVals) {
        Row r = new Row();
        r.addKeyVals(keyVals);
        return query(r);
    }

    public Query query(Map<String, Object> parameters) {
        logger.debug("Executing query '{}' ({})", statementName, sqlFile.getFileName());
        List<Object> orderedValues = Lists.newLinkedList();
        StringBuilder newLines = new StringBuilder();
        String[] lines = sqlString.split("\\n");
        for (String line : lines) {
            boolean match = false;
            Matcher matcher = QUOTED_QUERY_ARG.matcher(line);
            if (matcher.find()) {
                match = true;
            } else {
                matcher = UNQUOTED_QUERY_ARG.matcher(line);
                if (matcher.find()) {
                    match = true;
                }
            }

            if (match) {
                String paramValue = matcher.group(1);
                String paramName = matcher.group(2);
                if (parameters.containsKey(paramName)) {
                    Object value = parameters.get(paramName);
                    orderedValues.add(value);
                    newLines.append(line.substring(0, matcher.start(1)));
                    logger.debug("Parameter '{}': value '{}' changed to '{}'",paramName, paramValue, value);
                    newLines.append("?");
                    newLines.append(line.substring(matcher.end(1), line.length())).append("\n");
                } else {
                    newLines.append(line).append("\n");
                }
            } else {
                newLines.append(line).append("\n");
            }
        }

        String sql = newLines.toString();
        PreparedStatement ps = ConnectionUtils.prepareStatement(lSql, sql);
        for (int i = 0; i < orderedValues.size(); i++) {
            Object v = orderedValues.get(i);
            // TODO use converter chain sqlFileStatement -> sqlFile -> LSql
            JavaSqlConverter converter = lSql.getGlobalConverter();
            try {
                converter.setValueInStatement(ps, i + 1, v);
            } catch (Exception e) {
                throw new QueryException(e);
            }
        }
        return new Query(lSql, ps);
    }

}
