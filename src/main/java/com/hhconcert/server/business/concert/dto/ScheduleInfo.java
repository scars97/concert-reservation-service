package com.hhconcert.server.business.concert.dto;

import com.hhconcert.server.business.concert.domain.Schedule;

import java.time.LocalDate;

public record ScheduleInfo(
        Long id,
        ConcertInfo concert,
        LocalDate date
) {

    public static ScheduleInfo from(Schedule schedule) {
        return new ScheduleInfo(
                schedule.getId(),
                ConcertInfo.from(schedule.getConcert()),
                schedule.getDate()
        );
    }
}
