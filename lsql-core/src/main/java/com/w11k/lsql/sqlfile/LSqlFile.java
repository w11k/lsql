package com.w11k.lsql.sqlfile;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.w11k.lsql.LSql;
import com.w11k.lsql.converter.Converter;
import com.w11k.lsql.query.PlainQuery;
import com.w11k.lsql.query.PojoQuery;
import com.w11k.lsql.statement.AnnotatedSqlStatement;
import com.w11k.lsql.statement.AnnotatedSqlStatementToQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.ImmutableMap.copyOf;

public class LSqlFile {

    private static final Pattern STMT_BLOCK_BEGIN = Pattern.compile(
            "^--\\s*(.*)$",
            Pattern.MULTILINE);

    private static final Pattern STMT_BLOCK_END = Pattern.compile(
            ";\\s*$",
            Pattern.MULTILINE);

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LSql lSql;

    private final String fileNameForDescription; // without .sql extension

    private final String path;

    private final Map<String, AnnotatedSqlStatement> statements = Maps.newHashMap();

    public LSqlFile(LSql lSql, String fileNameForDescription, String path) {
        this.lSql = lSql;
        this.fileNameForDescription = fileNameForDescription;
        this.path = path;
        parseSqlStatements();
    }

    // ----- public -----

    public ImmutableMap<String, AnnotatedSqlStatement> getStatements() {
        return copyOf(statements);
    }

    public AnnotatedSqlStatementToQuery<PlainQuery> statement(String name) {
        final AnnotatedSqlStatement stmtToPs = getStatement(name);
        return new AnnotatedSqlStatementToQuery<PlainQuery>(stmtToPs) {
            @Override
            protected PlainQuery createQueryInstance(LSql lSql, PreparedStatement ps, Map<String, Converter> outConverters) {
                return new PlainQuery(LSqlFile.this.lSql, ps, outConverters);
            }
        };
    }

    @Deprecated
    public <T> AnnotatedSqlStatementToQuery<PojoQuery<T>> statement(String name, final Class<T> pojoClass) {
        final AnnotatedSqlStatement stmtToPs = getStatement(name);
        return new AnnotatedSqlStatementToQuery<PojoQuery<T>>(stmtToPs) {
            @Override
            protected PojoQuery<T> createQueryInstance(LSql lSql, PreparedStatement ps, Map<String, Converter> outConverters) {
                return new PojoQuery<>(LSqlFile.this.lSql, ps, pojoClass, outConverters);
            }
        };
    }

    public AnnotatedSqlStatement getSqlStatementToPreparedStatement(String name) {
        return this.getStatement(name);
    }

    private AnnotatedSqlStatement getStatement(String name) {
        if (!this.statements.containsKey(name)) {
            throw new IllegalArgumentException("No statement with name '" + name +
                    "' found in file '" + this.path + "'.");
        }
        return this.statements.get(name);
    }

    // ----- private -----

    private void parseSqlStatements() {
        logger.info("Reading SQL file '" + fileNameForDescription + "'");
        statements.clear();

        // a) try to load path via classloader
        InputStream is = getClass().getResourceAsStream(path);
        if (is == null) {
            try {
                // b) try to load path via file system
                is = new FileInputStream(path);
            } catch (FileNotFoundException e) {
                throw new RuntimeException("Unable to read file '" + path + "'");
            }
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String content = CharStreams.toString(reader);
            Matcher startMatcher = STMT_BLOCK_BEGIN.matcher(content);

            int endOfLastMatch = -1;
            while (startMatcher.find()) {
                if (startMatcher.start() <= endOfLastMatch) {
                    continue;
                }

                String stmtName = startMatcher.group(1);
                String typeAnnotation = "";
                if (stmtName.contains(":")) {
                    int idxColon = stmtName.indexOf(":");
                    typeAnnotation = stmtName.substring(idxColon + 1);
                    stmtName = stmtName.substring(0, idxColon);
                }

                String sub = content.substring(startMatcher.end());
                Matcher endMatcher = STMT_BLOCK_END.matcher(sub);
                if (!endMatcher.find()) {
                    throw new IllegalStateException(
                            "Could not find the end of the SQL expression '" +
                                    stmtName + "'. Did you add ';' at the end?");
                }
                sub = sub.substring(0, endMatcher.end()).trim();
                endOfLastMatch = startMatcher.start() + (startMatcher.end() - startMatcher.start()) + endMatcher.end();

                if (!this.areAllLinesCommented(sub)) {
                    logger.debug("Found SQL statement '{}'", stmtName);
                    AnnotatedSqlStatement stmt = new AnnotatedSqlStatement(
                            lSql, fileNameForDescription, stmtName, typeAnnotation, sub);
                    statements.put(stmtName, stmt);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean areAllLinesCommented(String sqlString) {
        Iterable<String> lines = Splitter.on("\n").split(sqlString);
        for (String line : lines) {
            if (!line.trim().startsWith("--")) {
                return false;
            }
        }
        return true;
    }

}
