package com.hhconcert.server.global.common.exception.definitions;

import com.hhconcert.server.global.common.error.PointErrorCode;
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
