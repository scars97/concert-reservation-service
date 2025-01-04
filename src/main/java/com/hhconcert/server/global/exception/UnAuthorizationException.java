package com.hhconcert.server.global.exception;

public class UnAuthorizationException extends RuntimeException{

    public UnAuthorizationException(String message) {
        super(message);
    }
}
