package com.hhconcert.server.application.dto;

import java.util.List;

public record ConcertScheduleResult(
        Long concertId,
        List<ScheduleResult> schedules
) {
    public static ConcertScheduleResult from(Long concertId, List<ScheduleResult> schedules) {
        return new ConcertScheduleResult(concertId, schedules);
    }
}
