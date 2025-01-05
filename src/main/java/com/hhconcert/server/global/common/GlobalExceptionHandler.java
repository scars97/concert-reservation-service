package com.hhconcert.server.global.common;

import com.hhconcert.server.global.exception.ConcertException;
import com.hhconcert.server.global.exception.PaymentException;
import com.hhconcert.server.global.exception.TokenException;
import com.hhconcert.server.global.exception.UnAuthorizationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConcertException.class)
    public ResponseEntity<ErrorResponse> handleConcertException(ConcertException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.toString(), ex.getMessage()));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<ErrorResponse> handlePaymentException(PaymentException ex) {
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                .body(new ErrorResponse(HttpStatus.PAYMENT_REQUIRED.toString(), ex.getMessage()));
    }

    @ExceptionHandler(UnAuthorizationException.class)
    public ResponseEntity<ErrorResponse> handleUnAuthorizationException(UnAuthorizationException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.toString(), ex.getMessage()));
    }

    @ExceptionHandler(TokenException.class)
    public ResponseEntity<ErrorResponse> handleTokenException(TokenException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN.toString(), ex.getMessage()));
    }
}
