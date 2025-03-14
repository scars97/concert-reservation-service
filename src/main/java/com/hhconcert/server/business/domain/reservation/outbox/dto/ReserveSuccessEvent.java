package com.hhconcert.server.business.domain.reservation.outbox.dto;

import com.hhconcert.server.business.domain.reservation.dto.ReservationInfo;

public record ReserveSuccessEvent(
        Long reserveId,
        String userId,
        String concertTitle,
        String schedule,
        String seatNumber,
        Integer price,
        String publishedAt
) {
    public static ReserveSuccessEvent of(ReservationInfo info) {
        return new ReserveSuccessEvent(
                info.reserveId(),
                info.user().userId(),
                info.concert().title(),
                info.schedule().date().toString(),
                info.seat().seatNumber(),
                info.price(),
                info.createAt().toString()
        );
    }
}
