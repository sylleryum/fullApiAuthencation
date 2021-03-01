package com.sylleryum.sylleryum.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;

/**
 * trims the incoming json requests
 */
@JsonComponent
public class WhiteSpaceRemovalDeserializer extends JsonDeserializer<String> {
    @Override
    public String deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
        System.out.println();
        return jp.getValueAsString().trim();
    }

}