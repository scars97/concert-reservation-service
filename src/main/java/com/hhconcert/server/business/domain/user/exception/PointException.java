package com.hhconcert.server.business.domain.user.exception;

import lombok.Getter;

@Getter
public class PointException extends RuntimeException {

    private final PointErrorCode errorCode;

    public PointException(PointErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
