package com.hhconcert.server.interfaces.api.point.dto;

import com.hhconcert.server.business.domain.user.dto.PointCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PointRequest (
        @NotNull(message = "사용자 ID는 필수 입력 값입니다.")
        @Schema(description = "사용자 ID",  example = "user1234")
        String userId,
        @NotNull(message = "가격은 필수 입력 값입니다.")
        @Positive(message = "가격은 0보다 큰 값이어야 합니다.")
        @Schema(description = "충전 금액",  example = "50000")
        int amount
){
    public PointCommand toCommand() {
        return new PointCommand(userId, amount);
    }
}
