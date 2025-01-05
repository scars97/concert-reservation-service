package com.hhconcert.server.interfaces.api.concert.dto;

public record SeatResponse (
        Long seatId,
        String seatNumber,
        Integer price,
        String available
) {
}
