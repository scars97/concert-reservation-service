package com.hhconcert.server.business.domain.payment.dto;

import com.hhconcert.server.business.domain.payment.entity.Payment;
import com.hhconcert.server.business.domain.payment.entity.PaymentStatus;
import com.hhconcert.server.business.domain.reservation.dto.ReservationResult;
import com.hhconcert.server.business.domain.user.dto.UserResult;

import java.time.LocalDateTime;

public record PaymentResult(
        Long paymentId,
        ReservationResult reserve,
        UserResult user,
        Integer price,
        PaymentStatus status,
        LocalDateTime createdAt
) {
    public static PaymentResult from(Payment payment) {
        return new PaymentResult(
                payment.getId(),
                ReservationResult.from(payment.getReservation()),
                UserResult.from(payment.getUser()),
                payment.getPrice(),
                payment.getStatus(),
                payment.getCreateAt()
        );
    }
}
