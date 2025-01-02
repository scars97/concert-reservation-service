package com.hhconcert.server.interfaces.api.concert.dto;

import com.hhconcert.server.interfaces.api.schedule.dto.ScheduleResponse;

import java.util.List;

public record ConcertScheduleResponse (
        Long concertId,
        List<ScheduleResponse> schedules
){
}
