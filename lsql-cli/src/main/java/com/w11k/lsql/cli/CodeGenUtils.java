package com.w11k.lsql.cli;

import com.google.common.base.Joiner;

public final class CodeGenUtils {

    public static String lowerCamelToUpperCamel(String name) {
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static void log(String ...strings) {
        String joined = Joiner.on(" ").join(strings);
        System.out.println(joined);
    }



}
