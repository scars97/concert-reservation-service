package com.hhconcert.server.global.common.exception.definitions;

public class UnAuthorizationException extends RuntimeException{

    public UnAuthorizationException(String message) {
        super(message);
    }
}
