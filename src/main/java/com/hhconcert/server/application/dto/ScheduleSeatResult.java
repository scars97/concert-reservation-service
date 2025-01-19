package com.hhconcert.server.application.dto;

import com.hhconcert.server.interfaces.api.concert.dto.SeatResponse;

import java.util.List;

public record ScheduleSeatResult(
        Long scheduleId,
        List<SeatResponse> seats
) {
    public static ScheduleSeatResult from(Long scheduleId, List<SeatResponse> responses) {
        return new ScheduleSeatResult(scheduleId, responses);
    }
}
