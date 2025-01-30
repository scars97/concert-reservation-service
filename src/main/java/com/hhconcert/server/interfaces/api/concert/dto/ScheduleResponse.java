package com.hhconcert.server.interfaces.api.concert.dto;

import com.hhconcert.server.application.dto.ScheduleResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record ScheduleResponse (
        @Schema(description = "스케줄 ID",  example = "1")
        Long scheduleId,
        @Schema(description = "공연 날짜", example = "2025-03-01")
        LocalDate date
) {
    public static ScheduleResponse from(ScheduleResult result) {
        return new ScheduleResponse(
                result.scheduleId(),
                result.date()
        );
    }
}
