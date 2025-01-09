package com.hhconcert.server.interfaces.api.payment.dto;

import com.hhconcert.server.business.domain.payment.dto.PaymentInfo;

public record PaymentRequest (
        String userId,
        Long reserveId,
        int amount
){
    public PaymentInfo toInfo() {
        return new PaymentInfo(userId, reserveId, amount);
    }
}
