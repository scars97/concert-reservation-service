package com.hhconcert.server.business.domain.user.dto;

import com.hhconcert.server.business.domain.user.entity.User;

public record PointInfo(
        String userId,
        Integer point
) {
    public static PointInfo from(User user) {
        return new PointInfo(user.getUserId(), user.getPoint());
    }
}
