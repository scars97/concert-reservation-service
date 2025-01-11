package com.hhconcert.server.business.domain.user.dto;

import com.hhconcert.server.business.domain.user.entity.User;

public record UserResult (
        String userId,
        Integer point
) {
    public static UserResult from(User user) {
        return new UserResult(
                user.getUserId(),
                user.getPoint()
        );
    }
}
