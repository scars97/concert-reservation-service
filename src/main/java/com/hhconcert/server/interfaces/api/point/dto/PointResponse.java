package com.hhconcert.server.interfaces.api.point.dto;

import com.hhconcert.server.application.dto.PointResult;

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
}
