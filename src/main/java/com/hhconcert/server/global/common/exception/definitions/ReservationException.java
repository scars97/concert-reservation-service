package com.hhconcert.server.global.common.exception.definitions;

import com.hhconcert.server.global.common.error.ReservationErrorCode;
import lombok.Getter;

@Getter
public class ReservationException extends RuntimeException {

    private ReservationErrorCode errorCode;

    public ReservationException(String message) {
        super(message);
    }

    public ReservationException(ReservationErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
