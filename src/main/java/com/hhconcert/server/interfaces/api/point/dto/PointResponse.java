package com.hhconcert.server.interfaces.api.point.dto;

import com.hhconcert.server.business.domain.user.dto.PointResult;
import com.hhconcert.server.business.domain.user.dto.UserResult;

public record PointResponse (
        String userId,
        int point
){
    public static PointResponse from(PointResult result) {
        return new PointResponse(
                result.userId(),
                result.point()
        );
    }

    public static PointResponse from(UserResult result) {
        return new PointResponse(
                result.userId(),
                result.point()
        );
    }
}
