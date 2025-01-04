package com.hhconcert.server.interfaces.api.concert.dto;

import java.time.LocalDate;

public record ScheduleResponse (
        Long scheduleId,
        LocalDate date
) {
}
