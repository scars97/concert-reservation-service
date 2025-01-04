package com.hhconcert.server.interfaces.api.concert.dto;

import java.util.List;

public record ConcertScheduleResponse (
        Long concertId,
        List<ScheduleResponse> schedules
){
}
