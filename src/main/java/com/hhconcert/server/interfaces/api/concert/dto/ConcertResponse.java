package com.hhconcert.server.interfaces.api.concert.dto;

import com.hhconcert.server.application.dto.ConcertResult;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

public record ConcertResponse (
        @Schema(description = "콘서트 ID",  example = "1")
        Long id,
        @Schema(description = "콘서트 제목",  example = "A 콘서트")
        String title,
        @Schema(description = "시작일")
        LocalDate startDate,
        @Schema(description = "종료일")
        LocalDate endDate
) {
    public static ConcertResponse from(ConcertResult result) {
        return new ConcertResponse(
                result.id(),
                result.title(),
                result.startDate(),
                result.endDate()
        );
    }
}
