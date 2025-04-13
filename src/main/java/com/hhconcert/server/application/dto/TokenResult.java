package com.hhconcert.server.application.dto;

import com.hhconcert.server.business.queues.dto.TokenInfo;
import com.hhconcert.server.business.queues.domain.TokenStatus;

import java.time.LocalDateTime;

public record TokenResult(
        String tokenId,
        String userId,
        Long priority,
        TokenStatus status,
        LocalDateTime tokenIssuedAt,
        LocalDateTime activeAt,
        LocalDateTime expireAt
) {
    public static TokenResult from(TokenInfo info) {
        return new TokenResult(
                info.tokenId(),
                info.userId(),
                info.priority(),
                info.status(),
                info.tokenIssuedAt(),
                info.activeAt(),
                info.expireAt()
        );
    }
}
