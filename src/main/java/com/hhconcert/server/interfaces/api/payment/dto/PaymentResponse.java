package com.hhconcert.server.interfaces.api.payment.dto;

import java.time.LocalDateTime;

public record PaymentResponse (
        Long paymentId,
        Long reserveId,
        String userId,
        Integer price,
        String status,
        LocalDateTime createdAt
){
}
