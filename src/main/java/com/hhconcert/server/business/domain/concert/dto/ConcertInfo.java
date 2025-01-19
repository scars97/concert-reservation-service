package com.hhconcert.server.business.domain.concert.dto;

import com.hhconcert.server.business.domain.concert.entity.Concert;

import java.time.LocalDate;

public record ConcertInfo(
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate
) {
    public static ConcertInfo from(Concert concert) {
        return new ConcertInfo(
                concert.getId(),
                concert.getTitle(),
                concert.getStartDate(),
                concert.getEndDate()
        );
    }
}
