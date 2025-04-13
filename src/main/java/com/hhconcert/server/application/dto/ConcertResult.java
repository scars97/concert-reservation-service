package com.hhconcert.server.application.dto;

import com.hhconcert.server.business.concert.dto.ConcertInfo;

import java.time.LocalDate;

public record ConcertResult(
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate
) {
    public static ConcertResult from(ConcertInfo info) {
        return new ConcertResult(
                info.id(),
                info.title(),
                info.startDate(),
                info.endDate()
        );
    }
}
