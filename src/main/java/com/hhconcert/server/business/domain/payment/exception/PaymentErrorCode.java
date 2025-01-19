package com.hhconcert.server.business.domain.payment.exception;

import com.hhconcert.server.global.common.error.ErrorDescriptor;
import org.springframework.http.HttpStatus;

public enum PaymentErrorCode implements ErrorDescriptor {

    NOT_MATCH_PAYMENT_AMOUNT(HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다.");

    private final HttpStatus status;
    private final String message;

    PaymentErrorCode(HttpStatus status, String message) {
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
