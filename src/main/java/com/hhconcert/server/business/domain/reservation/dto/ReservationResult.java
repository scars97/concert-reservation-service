package com.hhconcert.server.business.domain.reservation.dto;

import com.hhconcert.server.business.domain.concert.dto.ConcertResult;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.business.domain.schedule.dto.ScheduleResult;
import com.hhconcert.server.business.domain.seat.dto.SeatResult;
import com.hhconcert.server.business.domain.user.dto.UserResult;

import java.time.LocalDateTime;

public record ReservationResult(
        Long reserveId,
        UserResult user,
        ConcertResult concert,
        ScheduleResult schedule,
        SeatResult seat,
        Integer price,
        ReservationStatus status,
        LocalDateTime createAt,
        LocalDateTime expiredAt,
        LocalDateTime modified
) {
    public static ReservationResult from(Reservation reservation) {
        return new ReservationResult(
                reservation.getId(),
                UserResult.from(reservation.getUser()),
                ConcertResult.from(reservation.getConcert()),
                ScheduleResult.from(reservation.getSchedule()),
                SeatResult.from(reservation.getSeat()),
                reservation.getPrice(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getExpiredAt(),
                reservation.getModifiedAt()
        );
    }
}
