package com.w11k.lsql.cli;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.w11k.lsql.LSql;

import java.io.File;
import java.io.IOException;

public final class CodeGenUtils {

    public static String escapeSqlStringForJavaSourceFile(String line) {
        return line
                .replaceAll("\n", " \\\\n ")
                .replaceAll("\"", "\\\\\"");
    }

    public static String firstCharUpperCase(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static String createSaveNameForClass(String className) {
        int idx = 0;
        boolean lastWasLowerCase = false;
        while (idx < className.length()) {
            char ch = className.charAt(idx);
            if (!Character.isUpperCase(ch)) {
                lastWasLowerCase = true;
            } else {
                if (lastWasLowerCase) {
                    className = className.substring(0, idx)
                            + "_"
                            + className.substring(idx);
                }

                lastWasLowerCase = false;

            }
            idx++;
        }
        return firstCharUpperCase(className);
    }

    public static String joinStringsAsPackageName(String rootPackageName, String childPackageName) {
        if (Strings.isNullOrEmpty(childPackageName)) {
            return rootPackageName;
        } else {
            return rootPackageName + "." + childPackageName;
        }
    }

    public static File getFileFromBaseDirAndPackageName(File baseDir, String packageName) {

        Iterable<String> packageSegments = Splitter.on(".").split(packageName);
        String packagePath = Joiner.on(File.separatorChar).join(packageSegments);
        File target = new File(baseDir, packagePath);
//        try {
//            Files.createParentDirs(target);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        return target;
    }

    public static File getOutputFile(File exportRootDir, String packageName, String fileName) {
        File baseDir = getFileFromBaseDirAndPackageName(exportRootDir, packageName);
        return new File(baseDir, fileName);
    }

    public static void writeContent(String content, File target) {
        try {
            log("Writing", target.getAbsolutePath());
            Files.createParentDirs(target);
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

    public static void log(String... strings) {
        String joined = Joiner.on(" ").join(strings);
        System.out.println(joined);
    }

    public static void addSeperator(StringBuilder content) {
        content.append("\n    // ------------------------------------------------------------").append("\n\n");
    }

}
