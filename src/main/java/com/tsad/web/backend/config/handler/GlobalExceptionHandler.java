package com.tsad.web.backend.config.handler;

import com.tsad.web.backend.config.BusinessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, String>> handleBusinessException(BusinessException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("code", ex.getErrorCode());
        errorResponse.put("message", ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");
        return ResponseEntity.status(ex.getHttpStatus()).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("code", "Runtime Exception");
        errorResponse.put("message", ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        StackTraceElement stackTrace = ex.getStackTrace()[0];
        errorResponse.put("trace", String.format("%s (line: %s)", stackTrace.getClassName(), stackTrace.getLineNumber()));
        errorResponse.put("message", ex.getMessage());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-type", "application/json");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).headers(headers).body(errorResponse);
    }
}