package com.birkettenterprise.phonelocator.utility;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Created by alex on 29/03/14.
 */
public final class Mapper {
    private static ObjectMapper objectMapper;

    public static ObjectMapper get() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        }

        return objectMapper;
    }

    public static String string(Object data) {
        try {
            return get().writeValueAsString(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T objectOrThrow(String data, Class<T> type) throws JsonParseException, JsonMappingException, IOException {
        return get().readValue(data, type);
    }

    public static <T> T object(String data, Class<T> type) {
        try {
            return objectOrThrow(data, type);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}