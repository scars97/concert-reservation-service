package com.hhconcert.server.business.domain.concert.dto;

import com.hhconcert.server.business.domain.concert.entity.Seat;

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
