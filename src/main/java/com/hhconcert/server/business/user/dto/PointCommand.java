package com.hhconcert.server.business.user.dto;

public record PointCommand(
        String userId,
        Integer amount
) {
}
