package com.hhconcert.server.interfaces.api.queues.dto;

import com.hhconcert.server.business.domain.queues.dto.TokenResult;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;

import java.time.LocalDateTime;

public record TokenResponse(
        String tokenId,
        String userId,
        Integer priority,
        TokenStatus status,
        LocalDateTime createAt,
        LocalDateTime activeAt,
        LocalDateTime expireAt
) {

    public static TokenResponse from(TokenResult result) {
        return new TokenResponse(
                result.tokenId(),
                result.userId(),
                result.priority(),
                result.status(),
                result.createAt(),
                result.activeAt(),
                result.expireAt()
        );
    }
}
