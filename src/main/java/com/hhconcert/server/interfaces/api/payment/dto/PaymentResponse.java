package com.hhconcert.server.interfaces.api.payment.dto;

import com.hhconcert.server.business.domain.payment.dto.PaymentResult;
import com.hhconcert.server.business.domain.payment.entity.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResponse (
        Long paymentId,
        Long reserveId,
        String userId,
        Integer price,
        PaymentStatus status,
        LocalDateTime createdAt
){
    public static PaymentResponse from(PaymentResult result) {
        return new PaymentResponse(
                result.paymentId(),
                result.reserve().reserveId(),
                result.user().userId(),
                result.price(),
                result.status(),
                result.createdAt()
        );
    }
}
