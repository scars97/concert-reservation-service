package com.hhconcert.server.interfaces.api.concert.dto;

import com.hhconcert.server.application.dto.ScheduleResult;

import java.time.LocalDate;

public record ScheduleResponse (
        Long scheduleId,
        LocalDate date
) {
    public static ScheduleResponse from(ScheduleResult result) {
        return new ScheduleResponse(
                result.scheduleId(),
                result.date()
        );
    }
}
