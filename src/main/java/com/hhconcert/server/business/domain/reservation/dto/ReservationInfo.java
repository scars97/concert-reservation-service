package com.hhconcert.server.business.domain.reservation.dto;

import com.hhconcert.server.business.domain.concert.dto.ConcertInfo;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.business.domain.concert.dto.ScheduleInfo;
import com.hhconcert.server.business.domain.concert.dto.SeatInfo;
import com.hhconcert.server.business.domain.user.dto.UserInfo;

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
