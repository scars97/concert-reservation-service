package com.hhconcert.server.global.common.error;

import org.springframework.http.HttpStatus;

public enum TokenErrorCode implements ErrorDescriptor {

    DUPLICATED_TOKEN(HttpStatus.CONFLICT, "이미 토큰이 존재합니다.");

    private final HttpStatus status;
    private final String message;

    TokenErrorCode(HttpStatus status, String message) {
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
