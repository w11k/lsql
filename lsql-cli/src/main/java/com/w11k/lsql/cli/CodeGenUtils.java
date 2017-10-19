package com.w11k.lsql.cli;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.common.io.MoreFiles;
import com.w11k.lsql.LSql;

import java.io.File;
import java.io.IOException;

public final class CodeGenUtils {

    public static String lowerCamelToUpperCamel(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static void log(String ...strings) {
        String joined = Joiner.on(" ").join(strings);
        System.out.println(joined);
    }

    public static void writeContentIfChanged(String c, File pojoSourceFile) {
        try {
            MoreFiles.createParentDirectories(pojoSourceFile.toPath());

            // TODO only write if content changed
            Files.write(c.getBytes(Charsets.UTF_8), pojoSourceFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void rollback(LSql lSql) {
        try {
            lSql.getConnectionProvider().call().rollback();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
