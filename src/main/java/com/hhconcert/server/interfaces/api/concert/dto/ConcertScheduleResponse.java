package com.hhconcert.server.interfaces.api.concert.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ConcertScheduleResponse (
        @Schema(description = "콘서트 ID",  example = "1")
        Long concertId,
        List<ScheduleResponse> schedules
){
    public static ConcertScheduleResponse from(Long concertId, List<ScheduleResponse> schedules) {
        return new ConcertScheduleResponse(concertId, schedules);
    }
}
