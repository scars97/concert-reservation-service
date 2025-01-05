package com.hhconcert.server.interfaces.api.concert.dto;

import java.util.List;

public record ScheduleSeatResponse (
        Long scheduleId,
        List<SeatResponse> seats
){
}
