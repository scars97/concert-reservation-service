package com.hhconcert.server.business.domain.reservation.dto;

public record ReservationInfo(
        String userId,
        Long concertId,
        Long scheduleId,
        Long seatId
) {
}
