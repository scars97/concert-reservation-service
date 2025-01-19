package com.hhconcert.server.business.domain.queues.exception;

import lombok.Getter;

@Getter
public class TokenException extends RuntimeException {

    private TokenErrorCode errorCode;

    public TokenException(String message) {
        super(message);
    }

    public TokenException(TokenErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
