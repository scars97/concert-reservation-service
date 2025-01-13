package com.hhconcert.server.global.common.exception.definitions;

import com.hhconcert.server.global.common.error.TokenErrorCode;
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
