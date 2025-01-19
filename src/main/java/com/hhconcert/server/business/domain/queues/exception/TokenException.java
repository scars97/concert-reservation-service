package com.hhconcert.server.business.domain.queues.exception;

import lombok.Getter;

@Getter
public class TokenException extends RuntimeException {

    private final TokenErrorCode errorCode;

    public TokenException(TokenErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
