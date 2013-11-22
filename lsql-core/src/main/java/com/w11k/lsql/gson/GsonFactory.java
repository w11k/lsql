package com.w11k.lsql.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.joda.time.DateTime;

public class GsonFactory {

    public Gson createInstance() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DateTime.class, new DateTimeSerializer());
        builder.registerTypeAdapter(DateTime.class, new DateTimeDeserializer());
        return builder.create();
    }

}
