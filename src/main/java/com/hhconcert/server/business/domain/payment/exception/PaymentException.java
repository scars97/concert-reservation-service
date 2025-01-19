package com.hhconcert.server.business.domain.payment.exception;

import lombok.Getter;

@Getter
public class PaymentException extends RuntimeException {

    private PaymentErrorCode errorCode;

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(PaymentErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
