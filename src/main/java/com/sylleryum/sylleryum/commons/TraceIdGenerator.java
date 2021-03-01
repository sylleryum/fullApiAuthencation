package com.sylleryum.sylleryum.commons;

import java.util.Optional;
import java.util.UUID;


public class TraceIdGenerator {

    public static String generateTraceId(Optional<String> traceId){
        return traceId.orElse(UUID.randomUUID().toString());
    }

}
