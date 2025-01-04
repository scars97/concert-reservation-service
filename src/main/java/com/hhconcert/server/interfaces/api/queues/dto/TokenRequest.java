package com.hhconcert.server.interfaces.api.queues.dto;

public record TokenRequest (
        String userId,
        Long concertId
) {
}
