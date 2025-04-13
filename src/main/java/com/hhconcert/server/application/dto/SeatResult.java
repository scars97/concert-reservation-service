package com.hhconcert.server.application.dto;

import com.hhconcert.server.business.concert.dto.SeatInfo;

public record SeatResult(
        Long seatId,
        String seatNumber,
        Integer price
) {
    public static SeatResult from(SeatInfo info) {
        return new SeatResult(
                info.seatId(),
                info.seatNumber(),
                info.price()
        );
    }
}
