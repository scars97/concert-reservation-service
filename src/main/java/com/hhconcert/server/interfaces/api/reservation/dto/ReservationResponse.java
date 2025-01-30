package com.hhconcert.server.interfaces.api.reservation.dto;

import com.hhconcert.server.application.dto.ReservationResult;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.interfaces.api.concert.dto.ConcertResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record ReservationResponse (
        @Schema(description = "예약 ID",  example = "1")
        Long reserveId,
        @Schema(description = "콘서트 일자", example = "2025-03-01")
        LocalDate schedule,
        @Schema(description = "좌석 번호",  example = "A1")
        String seatNumber,
        ConcertResponse concert,
        @Schema(description = "예약 금액",  example = "75000")
        Integer price,
        @Schema(description = "예약 상태",  example = "TEMP")
        ReservationStatus status,
        @Schema(description = "예약 일시", example = "2025-01-30T12:00:00.000Z")
        LocalDateTime createdAt,
        @Schema(description = "임시 예약 만료 시간", example = "2025-01-30T12:05:00.000Z")
        LocalDateTime expiredAt
){
    public static ReservationResponse from(ReservationResult result) {
        return new ReservationResponse(
                result.reserveId(),
                result.schedule(),
                result.seatNumber(),
                ConcertResponse.from(result.concert()),
                result.price(),
                result.status(),
                result.createdAt(),
                result.expiredAt()
        );
    }
}
