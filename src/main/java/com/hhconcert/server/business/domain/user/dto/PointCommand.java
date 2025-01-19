package com.hhconcert.server.business.domain.user.dto;

public record PointCommand(
        String userId,
        Integer amount
) {
}
