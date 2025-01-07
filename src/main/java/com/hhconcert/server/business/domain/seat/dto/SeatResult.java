package com.hhconcert.server.business.domain.seat.dto;

import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.seat.entity.SeatStatus;

public record SeatResult(
        Long seatId,
        String seatNumber,
        Integer price,
        SeatStatus status
) {
    public static SeatResult from(Seat seat) {
        return new SeatResult(
                seat.getId(),
                seat.getSeatNumber(),
                seat.getPrice(),
                seat.getStatus()
        );
    }
}
