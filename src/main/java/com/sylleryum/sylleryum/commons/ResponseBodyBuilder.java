package com.sylleryum.sylleryum.commons;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * used to create json responses
 */
public class ResponseBodyBuilder {

    public static String build(Map<String, String> body) throws JsonProcessingException {

        String json = new ObjectMapper().writeValueAsString(body);

        return json;
    }

    public static String buildSingle(String key, String map) {

        StringBuilder response = new StringBuilder();
        response.append("{\"" + key + "\":\"" + map + "\"}");


        return response.toString();
    }
}
