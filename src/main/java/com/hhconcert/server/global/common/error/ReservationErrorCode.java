package com.hhconcert.server.global.common.error;

import org.springframework.http.HttpStatus;

public enum ReservationErrorCode implements ErrorDescriptor {

    INVALID_RESERVATION_STATUS(HttpStatus.CONFLICT, "결제할 수 없는 예약 내역입니다."),
    EXPIRED_RESERVATION(HttpStatus.GONE, "임시 예약 시간이 만료되었습니다.");

    private final HttpStatus status;
    private final String message;

    ReservationErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return this.status;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
