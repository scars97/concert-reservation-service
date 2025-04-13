package com.hhconcert.server.business.reservation.dto;

import com.hhconcert.server.business.concert.dto.ConcertInfo;
import com.hhconcert.server.business.concert.dto.ScheduleInfo;
import com.hhconcert.server.business.concert.dto.SeatInfo;
import com.hhconcert.server.business.reservation.domain.Reservation;
import com.hhconcert.server.business.reservation.domain.ReservationStatus;
import com.hhconcert.server.business.user.dto.UserInfo;

import java.time.LocalDateTime;

public record ReservationInfo(
        Long reserveId,
        UserInfo user,
        ConcertInfo concert,
        ScheduleInfo schedule,
        SeatInfo seat,
        Integer price,
        ReservationStatus status,
        LocalDateTime createAt,
        LocalDateTime expiredAt,
        LocalDateTime modified
) {
    public static ReservationInfo from(Reservation reservation) {
        return new ReservationInfo(
                reservation.getId(),
                UserInfo.from(reservation.getUser()),
                ConcertInfo.from(reservation.getConcert()),
                ScheduleInfo.from(reservation.getSchedule()),
                SeatInfo.from(reservation.getSeat()),
                reservation.getPrice(),
                reservation.getStatus(),
                reservation.getCreatedAt(),
                reservation.getExpiredAt(),
                reservation.getModifiedAt()
        );
    }
}
