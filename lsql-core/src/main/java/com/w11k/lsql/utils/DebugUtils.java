package com.w11k.lsql.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class DebugUtils {

    public static void prettyPrintJson(Object result) {
        GsonBuilder gb = new GsonBuilder();
        Gson g = gb.setPrettyPrinting().create();
        String s = g.toJson(result);
        System.out.println(s);
    }

}
