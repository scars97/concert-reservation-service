package com.hhconcert.server.interfaces.api.point.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotNull(message = "사용자 ID는 필수 입력 값입니다.")
        @Schema(description = "사용자 ID",  example = "user1234")
        String userId
) {
}
