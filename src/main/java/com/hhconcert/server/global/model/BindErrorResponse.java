package com.hhconcert.server.global.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public record BindErrorResponse(
        HttpStatus status,
        Map<String, String> reason
) {
    public static ResponseEntity<BindErrorResponse> of(HttpStatus status, Map<String, String> reason) {
        return ResponseEntity.status(status).body(new BindErrorResponse(status, reason));
    }
}
