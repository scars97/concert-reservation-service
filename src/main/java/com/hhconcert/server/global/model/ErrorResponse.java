package com.hhconcert.server.global.model;

import com.hhconcert.server.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public record ErrorResponse (
        HttpStatus status,
        String message
){
    public static ResponseEntity<ErrorResponse> of(ErrorCode error) {
        return ResponseEntity.status(error.getStatus())
                .body(new ErrorResponse(error.getStatus(), error.getMessage()));
    }
}
