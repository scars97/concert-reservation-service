package com.hhconcert.server.interfaces.api.concert.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ScheduleRequest(
        @NotNull(message = "ID는 필수 입력 값입니다.")
        @Positive(message = "ID 는 0보다 큰 값이어야 합니다.")
        Long scheduleId
) {
}
