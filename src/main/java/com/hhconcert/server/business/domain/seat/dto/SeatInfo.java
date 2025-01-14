package com.hhconcert.server.business.domain.seat.dto;

import com.hhconcert.server.business.domain.seat.entity.Seat;

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
