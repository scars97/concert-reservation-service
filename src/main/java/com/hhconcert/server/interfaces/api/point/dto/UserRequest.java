package com.hhconcert.server.interfaces.api.point.dto;

import jakarta.validation.constraints.NotNull;

public record UserRequest(
        @NotNull(message = "사용자 ID는 필수 입력 값입니다.")
        String userId
) {
}
