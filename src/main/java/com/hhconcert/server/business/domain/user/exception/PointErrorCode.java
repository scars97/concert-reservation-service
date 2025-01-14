package com.hhconcert.server.business.domain.user.exception;

import com.hhconcert.server.global.common.error.ErrorDescriptor;
import org.springframework.http.HttpStatus;

public enum PointErrorCode implements ErrorDescriptor {

    INSUFFICIENT_POINTS(HttpStatus.PAYMENT_REQUIRED, "잔액이 부족합니다.");

    private final HttpStatus status;
    private final String message;

    PointErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
