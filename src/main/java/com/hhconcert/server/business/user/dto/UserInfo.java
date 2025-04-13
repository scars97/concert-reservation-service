package com.hhconcert.server.business.user.dto;

import com.hhconcert.server.business.user.domain.User;

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
