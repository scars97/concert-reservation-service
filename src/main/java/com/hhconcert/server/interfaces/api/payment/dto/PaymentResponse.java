package com.hhconcert.server.interfaces.api.payment.dto;

import com.hhconcert.server.application.dto.PaymentResult;
import com.hhconcert.server.business.domain.payment.entity.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PaymentResponse (
        @Schema(description = "결제 ID",  example = "1")
        Long paymentId,
        @Schema(description = "예약 ID",  example = "1")
        Long reserveId,
        @Schema(description = "사용자 ID",  example = "user1234")
        String userId,
        @Schema(description = "결제 금액",  example = "75000")
        Integer price,
        @Schema(description = "결제 상태",  example = "SUCCESS")
        PaymentStatus status,
        @Schema(description = "결제 일시")
        LocalDateTime createdAt
){
    public static PaymentResponse from(PaymentResult result) {
        return new PaymentResponse(
                result.paymentId(),
                result.reserve().reserveId(),
                result.user().userId(),
                result.price(),
                result.status(),
                result.createdAt()
        );
    }
}
