package com.hhconcert.server.interfaces.api.concert.dto;

import com.hhconcert.server.business.domain.concert.dto.ConcertResult;

import java.time.LocalDate;

public record ConcertResponse (
        Long id,
        String title,
        LocalDate startDate,
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
