package com.hhconcert.server.interfaces.api.point.dto;

import com.hhconcert.server.business.domain.user.dto.PointInfo;

public record PointRequest (
        String userId,
        int amount
){
    public PointInfo toInfo() {
        return new PointInfo(userId, amount);
    }
}
