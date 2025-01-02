package com.hhconcert.server.exception;

public class UnAuthorizationException extends RuntimeException{

    public UnAuthorizationException(String message) {
        super(message);
    }
}
