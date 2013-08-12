package com.w11k.lsql;

import com.google.common.io.CharStreams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SqlFileReader {

    public static SqlFileReader fromRelativeToClass(Class clazz, String fileName) {
        String p = clazz.getPackage().getName();
        p = "/" + p.replaceAll("\\.", "/") + "/";
        InputStream is = clazz.getResourceAsStream(p + fileName);
        return new SqlFileReader(fileName, is);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String fileName;
    //private final Pattern namedStatementBlockPattern = Pattern.compile("^-- \\w*$\\n.*;$");
    private static final Pattern NAMED_STMT_BLOCK_PATTERN = Pattern.compile(
            "^XX (.*)$",
            Pattern.MULTILINE);

    public SqlFileReader(String fileName, InputStream is) {
        this.fileName = fileName;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String content = CharStreams.toString(reader);

            Matcher blocks = NAMED_STMT_BLOCK_PATTERN.matcher(content);

            System.out.println(blocks.groupCount());
            for (int i = 1; i <= blocks.groupCount(); i++) {
                String g = blocks.group(i);
                System.out.println(g);

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ----- getter/setter -----

    // ----- public -----

    // ----- private -----

}
