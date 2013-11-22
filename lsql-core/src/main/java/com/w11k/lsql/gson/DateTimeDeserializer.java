package com.w11k.lsql.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import org.joda.time.DateTime;

import java.lang.reflect.Type;

class DateTimeDeserializer implements JsonDeserializer<DateTime> {

    public DateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
            throws JsonParseException {

        return new DateTime(json.getAsJsonPrimitive().getAsString());
    }

}
