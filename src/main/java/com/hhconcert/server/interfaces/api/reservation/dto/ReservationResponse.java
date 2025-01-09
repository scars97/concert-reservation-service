package com.hhconcert.server.interfaces.api.reservation.dto;

import com.hhconcert.server.business.domain.reservation.dto.ReservationResult;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.interfaces.api.concert.dto.ConcertResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationResponse (
        Long reserveId,
        LocalDate schedule,
        String seatNumber,
        ConcertResponse concert,
        Integer price,
        ReservationStatus status,
        LocalDateTime createdAt,
        LocalDateTime expiredAt
){
    public static ReservationResponse from(ReservationResult result) {
        return new ReservationResponse(
                result.reserveId(),
                result.schedule().date(),
                result.seat().seatNumber(),
                ConcertResponse.from(result.concert()),
                result.price(),
                result.status(),
                result.createAt(),
                result.expiredAt()
        );
    }
}
