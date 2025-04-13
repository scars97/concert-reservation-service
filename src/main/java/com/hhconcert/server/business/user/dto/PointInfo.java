package com.hhconcert.server.business.user.dto;

import com.hhconcert.server.business.user.domain.User;

public record PointInfo(
        String userId,
        Integer point
) {
    public static PointInfo from(User user) {
        return new PointInfo(user.getUserId(), user.getPoint());
    }
}
