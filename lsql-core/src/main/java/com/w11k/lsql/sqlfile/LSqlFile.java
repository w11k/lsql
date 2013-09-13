package com.w11k.lsql.sqlfile;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.io.CharStreams;
import com.w11k.lsql.LSql;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.collect.ImmutableMap.copyOf;

public class LSqlFile {

    private static final Pattern STMT_BLOCK_BEGIN = Pattern.compile(
            "^--\\s*(\\w*)\\s*$",
            Pattern.MULTILINE);

    private static final Pattern STMT_BLOCK_END = Pattern.compile(
            ";\\s*$",
            Pattern.MULTILINE);

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LSql lSql;

    private final String fileName; // without .sql extension

    private final String path;

    private final Map<String, LSqlFileStatement> statements = Maps.newHashMap();

    public LSqlFile(LSql lSql, String fileName, String path) {
        this.lSql = lSql;
        this.fileName = fileName;
        this.path = path;
        parseSqlStatements();
    }

    public String getFileName() {
        return fileName;
    }

    // ----- public -----

    public ImmutableMap<String, LSqlFileStatement> getStatements() {
        return copyOf(statements);
    }

    public LSqlFileStatement statement(String name) {
        if (lSql.isReadSqlFilesOnEveryAccess()) {
            parseSqlStatements();
        }
        if (!statements.containsKey(name)) {
            throw new IllegalArgumentException("No statement with name '" + name + "' found.");
        }
        return statements.get(name);
    }

    // ----- private -----

    private void parseSqlStatements() {
        logger.info("Reading SQL file '" + fileName + "'");
        statements.clear();
        URL url = getClass().getResource(path);
        try {
            File file = new File(url.toURI());
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String content = CharStreams.toString(reader);
            Matcher startMatcher = STMT_BLOCK_BEGIN.matcher(content);
            while (startMatcher.find()) {
                String name = startMatcher.group(1);
                String sub = content.substring(startMatcher.end());
                Matcher endMatcher = STMT_BLOCK_END.matcher(sub);
                if (!endMatcher.find()) {
                    throw new IllegalStateException(
                            "Could not find the end of the SQL expression '" +
                                    name + "'. Did you add ';' at the end?");
                }
                sub = sub.substring(0, endMatcher.end()).trim();
                logger.debug("Found SQL statement '{}'", name);
                statements.put(name, new LSqlFileStatement(lSql, this, name, sub));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

}
