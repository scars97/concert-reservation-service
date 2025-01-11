package com.hhconcert.server.interfaces.api.concert.dto;

import com.hhconcert.server.business.domain.seat.dto.SeatResult;

public record SeatResponse (
        Long seatId,
        String seatNumber,
        Integer price
) {
    public static SeatResponse from(SeatResult result) {
        return new SeatResponse(
                result.seatId(),
                result.seatNumber(),
                result.price()
        );
    }
}
