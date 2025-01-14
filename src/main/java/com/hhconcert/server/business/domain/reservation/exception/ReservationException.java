package com.hhconcert.server.business.domain.reservation.exception;

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
