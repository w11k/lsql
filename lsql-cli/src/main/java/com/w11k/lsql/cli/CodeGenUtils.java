package com.w11k.lsql.cli;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Files;
import com.w11k.lsql.LSql;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class CodeGenUtils {

    public static String escapeSqlStringForJavaSourceFile(String line) {
        return line
                .replaceAll("\n", " \\\\n ")
                .replaceAll("\"", "\\\\\"");
    }

    public static String getJavaCodeName(String str,
                                         boolean insertUnderscoreOnCaseChange,
                                         boolean startUppercase) {

        StringBuilder result = new StringBuilder();

        if (str.length() == 1) {
            result.append(str);
        } else {
            int idx = 0;
            while (idx < str.length() - 1) {
                char ch = str.charAt(idx);
                if (!Character.isUpperCase(ch)) {
                    lastWasLowerCase = true;
                } else {
                    if (lastWasLowerCase) {
                        str = str.substring(0, idx)
                                + "_"
                                + str.substring(idx);
                    }

                    lastWasLowerCase = false;

                }
                idx++;
            }
        }



        return firstCharUpperCase(str);


//        IdentifierConverter identifierConverter = lSql.getConfig().getDialect().getIdentifierConverter();
//        CaseFormat toCaseFormat = identifierConverter.getToCaseFormat();
//        return toCaseFormat.to(lSql.getConfig().getCodeGenerationCaseFormat(), input);

    }

    public static String escapeSqlStringForJavaDoc(String line) {
        Iterable<String> lines = Splitter.on("\n").split(line);
        StringBuilder doc = new StringBuilder();
        for (String l : lines) {
            doc.append("\n     * ").append(l).append("<br>");
        }
        doc.append("\n");

        return doc.toString()
                .replaceAll("/\\*", "&#42;&#47;")
                .replaceAll("\\*/", "&#47;&#42;");
    }

    public static String firstCharUpperCase(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

//    public static String createSaveNameForClass(String className) {
//        int idx = 0;
//        boolean lastWasLowerCase = false;
//        while (idx < className.length()) {
//            char ch = className.charAt(idx);
//            if (!Character.isUpperCase(ch)) {
//                lastWasLowerCase = true;
//            } else {
//                if (lastWasLowerCase) {
//                    className = className.substring(0, idx)
//                            + "_"
//                            + className.substring(idx);
//                }
//
//                lastWasLowerCase = false;
//
//            }
//            idx++;
//        }
//        return firstCharUpperCase(className);
//    }

    public static String joinStringsAsPackageName(String... packageNames) {
        List<String> packages = Arrays.stream(packageNames)
                .map(p -> p.trim().equals("") ? null : p) // change '' to null so that `skipNulls()` can filter
                .collect(Collectors.toList());

        return Joiner.on(".").skipNulls().join(packages);
    }

    public static String getSubPathFromFileInParent(String parent, String childInParent) {
        int start = childInParent.lastIndexOf(parent) + parent.length();
        String relativePath = childInParent.substring(start);
        Iterable<String> pathSlitIt = Splitter.on(File.separatorChar).omitEmptyStrings().split(relativePath);
        List<String> pathSplit = Lists.newLinkedList(pathSlitIt);
        pathSplit.remove(pathSplit.size() - 1); // remove filename
        return Joiner.on(".").join(pathSplit);

    }

    public static File getFileFromBaseDirAndPackageName(File baseDir, String packageName) {
        Iterable<String> packageSegments = Splitter.on(".").split(packageName);
        String packagePath = Joiner.on(File.separatorChar).join(packageSegments);
        File target = new File(baseDir, packagePath);
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

}
