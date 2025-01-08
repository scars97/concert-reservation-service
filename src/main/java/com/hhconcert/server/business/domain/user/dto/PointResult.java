package com.hhconcert.server.business.domain.user.dto;

import com.hhconcert.server.business.domain.user.entity.User;

public record PointResult(
        String userId,
        Integer point
) {
    public static PointResult from(User user) {
        return new PointResult(user.getId(), user.getPoint());
    }
}
