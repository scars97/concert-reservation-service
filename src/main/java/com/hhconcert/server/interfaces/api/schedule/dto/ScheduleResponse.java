package com.hhconcert.server.interfaces.api.schedule.dto;

import java.time.LocalDate;

public record ScheduleResponse (
        Long scheduleId,
        LocalDate date
) {
}
