package com.hhconcert.server.interfaces.api.reservation.dto;

public record ReservationRequest (
        String userId,
        Long concertId,
        Long scheduleId,
        Long seatId
){
}
