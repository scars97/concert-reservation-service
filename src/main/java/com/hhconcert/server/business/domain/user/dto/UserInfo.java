package com.hhconcert.server.business.domain.user.dto;

import com.hhconcert.server.business.domain.user.entity.User;

public record UserInfo(
        String userId,
        Integer point
) {
    public static UserInfo from(User user) {
        return new UserInfo(
                user.getUserId(),
                user.getPoint()
        );
    }
}
