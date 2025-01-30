package com.hhconcert.server.interfaces.api.concert.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record ScheduleSeatResponse (
        @Schema(description = "스케줄 ID",  example = "1")
        Long scheduleId,
        List<SeatResponse> seats
){
    public static ScheduleSeatResponse from(Long scheduleId, List<SeatResponse> responses) {
        return new ScheduleSeatResponse(scheduleId, responses);
    }
}
