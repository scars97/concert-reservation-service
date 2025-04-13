package com.hhconcert.server.business.payment.dto;

public record PaymentCommand(
        String userId,
        Long reserveId,
        int amount
) {
}
