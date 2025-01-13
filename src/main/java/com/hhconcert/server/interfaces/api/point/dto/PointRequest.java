package com.hhconcert.server.interfaces.api.point.dto;

import com.hhconcert.server.business.domain.user.dto.PointInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PointRequest (
        @NotNull(message = "사용자 ID는 필수 입력 값입니다.")
        String userId,
        @NotNull(message = "가격은 필수 입력 값입니다.")
        @Positive(message = "가격은 0보다 큰 값이어야 합니다.")
        int amount
){
    public PointInfo toInfo() {
        return new PointInfo(userId, amount);
    }
}
