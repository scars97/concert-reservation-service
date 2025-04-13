package com.hhconcert.server.business.payment.dto;

import com.hhconcert.server.business.payment.domain.Payment;
import com.hhconcert.server.business.payment.domain.PaymentStatus;
import com.hhconcert.server.business.reservation.dto.ReservationInfo;
import com.hhconcert.server.business.user.dto.UserInfo;

import java.time.LocalDateTime;

public record PaymentInfo(
        Long paymentId,
        ReservationInfo reserve,
        UserInfo user,
        Integer price,
        PaymentStatus status,
        LocalDateTime createdAt
) {
    public static PaymentInfo from(Payment payment) {
        return new PaymentInfo(
                payment.getId(),
                ReservationInfo.from(payment.getReservation()),
                UserInfo.from(payment.getUser()),
                payment.getPrice(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }
}
