package com.hhconcert.server.business.domain.seat.dto;

import com.hhconcert.server.business.domain.seat.entity.Seat;

public record SeatResult(
        Long seatId,
        String seatNumber,
        Integer price
) {
    public static SeatResult from(Seat seat) {
        return new SeatResult(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getPrice()
        );
    }
}
