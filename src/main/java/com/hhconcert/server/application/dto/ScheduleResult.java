package com.hhconcert.server.application.dto;

import com.hhconcert.server.business.concert.dto.ScheduleInfo;

import java.time.LocalDate;

public record ScheduleResult(
        Long scheduleId,
        ConcertResult concert,
        LocalDate date
) {
    public static ScheduleResult from(ScheduleInfo info) {
        return new ScheduleResult(
                info.id(),
                ConcertResult.from(info.concert()),
                info.date()
        );
    }
}
