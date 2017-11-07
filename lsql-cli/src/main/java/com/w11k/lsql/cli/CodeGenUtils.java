package com.w11k.lsql.cli;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import com.google.common.io.MoreFiles;
import com.w11k.lsql.LSql;

import java.io.File;
import java.io.IOException;

public final class CodeGenUtils {

    public static String firstCharUpperCase(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static void log(String... strings) {
        String joined = Joiner.on(" ").join(strings);
        System.out.println(joined);
    }

    public static void writeContentIfChanged(String content, File target) {
        try {
            if (target.isFile()) {
                String existing = MoreFiles.asCharSource(target.toPath(), Charsets.UTF_8).read();
                if (existing.equals(content)) {
                    return;
                }
            }

            log("Writing", target.getAbsolutePath());
            MoreFiles.createParentDirectories(target.toPath());
            Files.write(content.getBytes(Charsets.UTF_8), target);
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
