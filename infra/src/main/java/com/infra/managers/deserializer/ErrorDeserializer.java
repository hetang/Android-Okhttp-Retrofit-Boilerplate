package com.infra.managers.deserializer;

import com.infra.managers.models.Error;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class ErrorDeserializer implements JsonDeserializer<Error> {
    @Override
    public Error deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject jsonObject = json.getAsJsonObject();

        JsonObject errorObject = jsonObject.getAsJsonObject("error");
        return new Error(errorObject.get("code").getAsInt(), errorObject.get("message").getAsString());
    }
}