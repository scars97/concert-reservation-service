package com.hhconcert.server.interfaces.api.point.dto;

import com.hhconcert.server.application.dto.PointResult;
import io.swagger.v3.oas.annotations.media.Schema;

public record PointResponse (
        @Schema(description = "사용자 ID",  example = "user1234")
        String userId,
        @Schema(description = "보유 금액",  example = "50000")
        int point
){
    public static PointResponse from(PointResult result) {
        return new PointResponse(
                result.userId(),
                result.point()
        );
    }
}
