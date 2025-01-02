package com.hhconcert.server.interfaces.api.queues.dto;

public record TokenResponse(
        String tokenId,
        String userId,
        Long concertId,
        Integer priority,
        String status
) {
}
