package com.hhconcert.server.business.domain.reservation.dto;

public record ReservationCommand(
        String userId,
        Long concertId,
        Long scheduleId,
        Long seatId
) {
}
