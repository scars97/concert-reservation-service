package com.hhconcert.server.interfaces.api.concert.dto;

import java.time.LocalDate;

public record ConcertResponse (
        Long id,
        String title,
        LocalDate startDate,
        LocalDate endDate
) {
}
