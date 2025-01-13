package com.hhconcert.server.global.common.exception.definitions;

import com.hhconcert.server.global.common.error.PaymentErrorCode;
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
