package com.hhconcert.server.interfaces.api.reservation.dto;

import com.hhconcert.server.business.domain.reservation.dto.ReservationInfo;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservationRequest (
        @NotNull(message = "사용자 ID는 필수 입력 값입니다.")
        String userId,
        @NotNull(message = "콘서트 ID는 필수 입력 값입니다.")
        @Positive(message = "콘서트 ID 는 0보다 큰 값이어야 합니다.")
        Long concertId,
        @NotNull(message = "일정 ID는 필수 입력 값입니다.")
        @Positive(message = "일정 ID 는 0보다 큰 값이어야 합니다.")
        Long scheduleId,
        @NotNull(message = "좌석 ID는 필수 입력 값입니다.")
        @Positive(message = "좌석 ID 는 0보다 큰 값이어야 합니다.")
        Long seatId
){
    public ReservationInfo toInfo() {
        return new ReservationInfo(userId, concertId, scheduleId, seatId);
    }
}
