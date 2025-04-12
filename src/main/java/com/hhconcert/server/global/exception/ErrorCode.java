package com.hhconcert.server.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // Payment
    NOT_MATCH_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다."),

    // Token
    DUPLICATED_TOKEN(HttpStatus.CONFLICT, "이미 토큰이 존재합니다."),
    TOKEN_IS_MISSING(HttpStatus.UNAUTHORIZED, "토큰 정보가 누락되었습니다."),
    INVALID_TOKEN(HttpStatus.FORBIDDEN, "잘못된 토큰입니다."),
    UNREGISTERED_TOKEN(HttpStatus.NOT_FOUND, "등록되지 않은 토큰입니다."),
    TOKEN_IS_UNAVAILABLE(HttpStatus.FORBIDDEN, "이용 가능한 토큰이 아닙니다."),

    // Reservation
    INVALID_RESERVATION_STATUS(HttpStatus.CONFLICT, "결제할 수 없는 예약 내역입니다."),
    EXPIRED_RESERVATION(HttpStatus.GONE, "임시 예약 시간이 만료되었습니다."),
    ALREADY_RESERVED(HttpStatus.CONFLICT, "이미 예약된 좌석입니다."),

    // UserPoint
    INSUFFICIENT_POINTS(HttpStatus.PAYMENT_REQUIRED, "잔액이 부족합니다.");

    private final HttpStatus status;
    private final String message;

    ErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

}
