package com.hhconcert.server.application.dto;

import com.hhconcert.server.business.domain.payment.dto.PaymentInfo;
import com.hhconcert.server.business.domain.payment.entity.PaymentStatus;

import java.time.LocalDateTime;

public record PaymentResult(
        Long paymentId,
        ReservationResult reserve,
        UserResult user,
        Integer price,
        PaymentStatus status,
        LocalDateTime createdAt
) {
    public static PaymentResult from(PaymentInfo info) {
        return new PaymentResult(
                info.paymentId(),
                ReservationResult.from(info.reserve()),
                UserResult.from(info.user()),
                info.price(),
                info.status(),
                info.createdAt()
        );
    }
}
