package com.sylleryum.sylleryum.exception;

public class TokenException extends Exception{

    private String traceId;

    public TokenException(String traceId, String message) {
        super(message);
        this.traceId = traceId;
    }

    public String getTraceId() {
        return traceId;
    }
}