package com.hhconcert.server.business.domain.concert.dto;

import com.hhconcert.server.business.domain.concert.entity.Concert;

import java.time.LocalDate;

public record ConcertResult (
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate
) {
    public static ConcertResult from(Concert concert) {
        return new ConcertResult(
                concert.getId(),
                concert.getTitle(),
                concert.getStartDate(),
                concert.getEndDate()
        );
    }
}
