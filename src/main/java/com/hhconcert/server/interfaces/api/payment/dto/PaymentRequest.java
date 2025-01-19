package com.hhconcert.server.interfaces.api.payment.dto;

import com.hhconcert.server.business.domain.payment.dto.PaymentCommand;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PaymentRequest (
        @NotNull(message = "사용자 ID는 필수 입력 값입니다.")
        String userId,
        @NotNull(message = "예약 ID는 필수 입력 값입니다.")
        @Positive(message = "예약 ID 는 0보다 큰 값이어야 합니다.")
        Long reserveId,
        @NotNull(message = "가격은 필수 입력 값입니다.")
        @Positive(message = "가격은 0보다 큰 값이어야 합니다.")
        int amount
){
    public PaymentCommand toCommand() {
        return new PaymentCommand(userId, reserveId, amount);
    }
}
