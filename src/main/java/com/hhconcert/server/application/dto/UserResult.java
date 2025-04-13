package com.hhconcert.server.application.dto;

import com.hhconcert.server.business.user.dto.UserInfo;

public record UserResult(
        String userId,
        Integer point
) {
    public static UserResult from(UserInfo info) {
        return new UserResult(
                info.userId(),
                info.point()
        );
    }
}
