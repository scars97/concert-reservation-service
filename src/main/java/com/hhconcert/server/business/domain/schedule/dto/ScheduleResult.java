package com.hhconcert.server.business.domain.schedule.dto;

import com.hhconcert.server.business.domain.concert.dto.ConcertResult;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;

import java.time.LocalDate;

public record ScheduleResult(
        Long id,
        ConcertResult concert,
        LocalDate date
) {

    public static ScheduleResult from(Schedule schedule) {
        return new ScheduleResult(
                schedule.getId(),
                ConcertResult.from(schedule.getConcert()),
                schedule.getDate()
        );
    }
}
