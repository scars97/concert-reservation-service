package com.hhconcert.server.business.concert.dto;

import com.hhconcert.server.business.concert.domain.Seat;

public record SeatInfo(
        Long seatId,
        String seatNumber,
        Integer price
) {
    public static SeatInfo from(Seat seat) {
        return new SeatInfo(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getPrice()
        );
    }
}
