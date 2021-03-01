package com.sylleryum.sylleryum.controller;

import com.sylleryum.sylleryum.entity.ApiError;
import com.sylleryum.sylleryum.exception.ResourceAlreadyExistsException;
import com.sylleryum.sylleryum.exception.ResourceNotFoundException;
import com.sylleryum.sylleryum.exception.TokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Optional;
import java.util.UUID;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    private static Logger logger = LoggerFactory.getLogger(ExceptionHandlerController.class);

    @ExceptionHandler(ResourceNotFoundException.class)
    public final ResponseEntity<ApiError> handleResourceNotFoundException(
            ResourceNotFoundException e, WebRequest webRequest) {

        logger.error(e.getTraceId()+" - "+e.getMessage());
        System.out.println();
        return new ResponseEntity<>(new ApiError(e.getTraceId(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenException.class)
    public final ResponseEntity<ApiError> handleTokenException(
            TokenException e, WebRequest webRequest) {

        logger.error(e.getTraceId()+" - "+e.getMessage());
        return new ResponseEntity<>(new ApiError(e.getTraceId(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public final ResponseEntity<ApiError> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException e, WebRequest webRequest) {

        logger.error(e.getTraceId()+" - "+e.getMessage());
        return new ResponseEntity<>(new ApiError(e.getTraceId(), e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DisabledException.class)
    public final ResponseEntity<ApiError> handleDisabledException(
            DisabledException e, WebRequest webRequest) {

        String traceId = getTraceId(webRequest);
        logger.error(traceId, e);
        return new ResponseEntity<>(new ApiError(traceId, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<ApiError> handleOtherException(
            Exception e, WebRequest webRequest) {

        String traceId = getTraceId(webRequest);
        logger.error(traceId, e);
        return new ResponseEntity<>(new ApiError(traceId, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    private String getTraceId(WebRequest webRequest) {
        Optional<String> traceId = Optional.ofNullable(webRequest.getHeader("trace-id"));
        return traceId.orElse(UUID.randomUUID().toString());
    }

}
