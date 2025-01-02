package com.hhconcert.server.interfaces.api.reservation.dto;

import com.hhconcert.server.interfaces.api.concert.dto.ConcertResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationResponse (
        Long reserveId,
        LocalDate schedule,
        String seatNumber,
        ConcertResponse concert,
        Integer price,
        String status,
        LocalDateTime reservedAt,
        LocalDateTime expiredAt
){
}
