package com.w11k.lsql.utils;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;

import java.io.IOException;

public class DebugUtils {

    public static void prettyPrintJson(Object result) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        try {
            String out = writer.writeValueAsString(result);
            System.out.println(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
