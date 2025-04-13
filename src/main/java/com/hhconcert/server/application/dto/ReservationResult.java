package com.hhconcert.server.application.dto;

import com.hhconcert.server.business.reservation.dto.ReservationInfo;
import com.hhconcert.server.business.reservation.domain.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationResult(
        Long reserveId,
        LocalDate schedule,
        String seatNumber,
        ConcertResult concert,
        Integer price,
        ReservationStatus status,
        LocalDateTime createdAt,
        LocalDateTime expiredAt
) {
    public static ReservationResult from(ReservationInfo info) {
        return new ReservationResult(
                info.reserveId(),
                info.schedule().date(),
                info.seat().seatNumber(),
                ConcertResult.from(info.concert()),
                info.price(),
                info.status(),
                info.createAt(),
                info.expiredAt()
        );
    }
}
