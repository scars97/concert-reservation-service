package com.hhconcert.server.interfaces.api.concert.dto;

import com.hhconcert.server.application.dto.SeatResult;
import io.swagger.v3.oas.annotations.media.Schema;

public record SeatResponse (
        @Schema(description = "좌석 ID",  example = "1")
        Long seatId,
        @Schema(description = "좌석 번호",  example = "A1")
        String seatNumber,
        @Schema(description = "좌석 가격",  example = "75000")
        Integer price
) {
    public static SeatResponse from(SeatResult result) {
        return new SeatResponse(
                result.seatId(),
                result.seatNumber(),
                result.price()
        );
    }
}
