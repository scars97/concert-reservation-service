package com.hhconcert.server.interfaces.api.concert.dto;

import java.util.List;

public record ConcertScheduleResponse (
        Long concertId,
        List<ScheduleResponse> schedules
){
    public static ConcertScheduleResponse from(Long concertId, List<ScheduleResponse> schedules) {
        return new ConcertScheduleResponse(concertId, schedules);
    }
}
