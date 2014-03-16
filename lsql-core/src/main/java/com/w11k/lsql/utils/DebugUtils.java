package com.w11k.lsql.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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
