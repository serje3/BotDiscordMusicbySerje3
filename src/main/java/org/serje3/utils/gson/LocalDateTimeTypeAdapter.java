package org.serje3.utils.gson;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.time.LocalDateTime;

public class LocalDateTimeTypeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
    @Override
    public LocalDateTime deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return LocalDateTime.parse(jsonElement.getAsString());
    }

    @Override
    public JsonElement serialize(LocalDateTime localDateTime, Type type, JsonSerializationContext jsonSerializationContext) {
        return new JsonPrimitive(localDateTime.toString());
    }
}
