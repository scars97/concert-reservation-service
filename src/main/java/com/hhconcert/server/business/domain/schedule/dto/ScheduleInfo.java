package com.hhconcert.server.business.domain.schedule.dto;

import com.hhconcert.server.business.domain.concert.dto.ConcertInfo;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;

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
