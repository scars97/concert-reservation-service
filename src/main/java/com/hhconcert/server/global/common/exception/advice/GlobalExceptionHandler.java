package com.hhconcert.server.global.common.exception.advice;

import com.hhconcert.server.global.common.exception.definitions.PaymentException;
import com.hhconcert.server.global.common.exception.definitions.PointException;
import com.hhconcert.server.global.common.exception.definitions.TokenException;
import com.hhconcert.server.global.common.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElementException(NoSuchElementException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND, e.getMessage()));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException ex) {
        log.error("Payment Exception occurred : {}", ex.getErrorCode());
        return ErrorResponse.of(ex.getErrorCode());
    }

    @ExceptionHandler(PointException.class)
    public ResponseEntity<ErrorResponse> handlePointException(PointException ex) {
        log.error("Point Exception occurred : {}", ex.getErrorCode());
        return ErrorResponse.of(ex.getErrorCode());
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ErrorResponse> handleTokenException(TokenException ex) {
        log.error("Token Exception occurred : {}", ex.getErrorCode());
        return ErrorResponse.of(ex.getErrorCode());
    }
}
