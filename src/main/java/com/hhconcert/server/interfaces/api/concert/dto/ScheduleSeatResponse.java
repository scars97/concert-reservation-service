package com.hhconcert.server.interfaces.api.concert.dto;

import java.util.List;

public record ScheduleSeatResponse (
        Long scheduleId,
        List<SeatResponse> seats
){
    public static ScheduleSeatResponse from(Long scheduleId, List<SeatResponse> responses) {
        return new ScheduleSeatResponse(scheduleId, responses);
    }
}
