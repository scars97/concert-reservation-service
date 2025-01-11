package com.hhconcert.server.interfaces.api.reservation.dto;

import com.hhconcert.server.business.domain.reservation.dto.ReservationInfo;

public record ReservationRequest (
        String userId,
        Long concertId,
        Long scheduleId,
        Long seatId
){
    public ReservationInfo toInfo() {
        return new ReservationInfo(userId, concertId, scheduleId, seatId);
    }
}
