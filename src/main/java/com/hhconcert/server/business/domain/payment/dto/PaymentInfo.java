package com.hhconcert.server.business.domain.payment.dto;

public record PaymentInfo(
        String userId,
        Long reserveId,
        int amount
) {
}
