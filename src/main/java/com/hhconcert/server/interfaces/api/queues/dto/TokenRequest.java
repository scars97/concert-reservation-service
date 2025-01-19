package com.hhconcert.server.interfaces.api.queues.dto;

import jakarta.validation.constraints.NotNull;

public record TokenRequest (
        @NotNull(message = "사용자 ID는 필수 입력 값입니다.")
        String userId
) {
}
