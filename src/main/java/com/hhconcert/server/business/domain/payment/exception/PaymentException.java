package com.hhconcert.server.business.domain.payment.exception;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {

    private final PaymentErrorCode errorCode;

    public PaymentException(PaymentErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
