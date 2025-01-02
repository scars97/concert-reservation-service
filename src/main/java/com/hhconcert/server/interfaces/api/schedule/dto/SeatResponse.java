package com.hhconcert.server.interfaces.api.schedule.dto;

public record SeatResponse (
        Long seatId,
        String seatNumber,
        Integer price,
        String available
) {
}
