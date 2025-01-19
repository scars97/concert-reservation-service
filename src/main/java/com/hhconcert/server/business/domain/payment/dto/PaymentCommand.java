package com.hhconcert.server.business.domain.payment.dto;

public record PaymentCommand(
        String userId,
        Long reserveId,
        int amount
) {
}
