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

    public static SqlFileReader relativeToClass(Class clazz, String fileName) {
        String p = clazz.getPackage().getName();
        p = "/" + p.replaceAll("\\.", "/") + "/";
        InputStream is = clazz.getResourceAsStream(p + fileName);
        return new SqlFileReader(fileName, is);
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String fileName;
    //private final Pattern namedStatementBlockPattern = Pattern.compile("^-- \\w*$\\n.*;$");

    private static final String NEWLINE = "((\\r\\n)|(\\n))";

    /*
    private static final Pattern NAMED_STMT_BLOCK_PATTERN = Pattern.compile(
            "^-- (.*)$" + NEWLINE + "([\\w\\W" + NEWLINE  + "]*;\\s*$)",
            Pattern.MULTILINE //| Pattern.DOTALL
    );
    */

    private static final Pattern NAMED_STMT_BLOCK_PATTERN = Pattern.compile(
            "^--(\\w*)(.*(?!--));\\s*$",
            Pattern.MULTILINE | Pattern.DOTALL
    );

    public SqlFileReader(String fileName, InputStream is) {
        this.fileName = fileName;
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        try {
            String content = CharStreams.toString(reader);
            Matcher match = NAMED_STMT_BLOCK_PATTERN.matcher(content);

            while (match.find()) {
                System.out.println("####################################################################");
                for (int i = 1; i <= match.groupCount(); i++) {
                    String group = match.group(i);
                    if (group != null && !"".equals(group.trim())) {
                        System.out.println("group(" + i + ") = {" + group + "}");
                    }
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // ----- getter/setter -----

    // ----- public -----

    // ----- private -----

}
