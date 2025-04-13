package com.hhconcert.server.application.dto;

import com.hhconcert.server.business.user.dto.PointInfo;
import com.hhconcert.server.business.user.dto.UserInfo;

public record PointResult(
        String userId,
        Integer point
) {
    public static PointResult from(PointInfo info) {
        return new PointResult(
                info.userId(),
                info.point()
        );
    }

    public static PointResult from(UserInfo info) {
        return new PointResult(
                info.userId(),
                info.point()
        );
    }
}
