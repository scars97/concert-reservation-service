package com.hhconcert.server.global.common.exception;

public class UnAuthorizationException extends RuntimeException{

    public UnAuthorizationException(String message) {
        super(message);
    }
}
