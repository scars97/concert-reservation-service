package com.hhconcert.server.application.dto;

import com.hhconcert.server.business.domain.queues.dto.TokenInfo;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;

import java.time.LocalDateTime;

public record TokenResult(
        String tokenId,
        String userId,
        Integer priority,
        TokenStatus status,
        LocalDateTime createAt,
        LocalDateTime activeAt,
        LocalDateTime expireAt
) {
    public static TokenResult from(TokenInfo info) {
        return new TokenResult(
                info.tokenId(),
                info.userId(),
                info.priority(),
                info.status(),
                info.createAt(),
                info.activeAt(),
                info.expireAt()
        );
    }
}
