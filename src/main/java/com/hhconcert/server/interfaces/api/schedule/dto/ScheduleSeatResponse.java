package com.hhconcert.server.interfaces.api.schedule.dto;

import java.util.List;

public record ScheduleSeatResponse (
        Long scheduleId,
        List<SeatResponse> seats
){
}
