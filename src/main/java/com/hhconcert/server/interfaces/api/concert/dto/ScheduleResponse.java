package com.hhconcert.server.interfaces.api.concert.dto;

import com.hhconcert.server.business.domain.schedule.dto.ScheduleResult;

import java.time.LocalDate;

public record ScheduleResponse (
        Long scheduleId,
        LocalDate date
) {
    public static ScheduleResponse from(ScheduleResult result) {
        return new ScheduleResponse(
                result.id(),
                result.date()
        );
    }
}
