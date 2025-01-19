package com.hhconcert.server.business.domain.reservation.exception;

import lombok.Getter;

@Getter
public class ReservationException extends RuntimeException {

    private final ReservationErrorCode errorCode;

    public ReservationException(ReservationErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
