package com.hhconcert.server.interfaces.api.payment.dto;

public record PaymentRequest (
        String userId,
        Long reserveId,
        Integer amount
){
}
