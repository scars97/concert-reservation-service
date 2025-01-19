package com.hhconcert.server.business.domain.user.exception;

import lombok.Getter;

@Getter
public class PointException extends RuntimeException {

    private PointErrorCode errorCode;

    public PointException(String message) {
        super(message);
    }

    public PointException(PointErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
