package com.hhconcert.server.business.domain.queues.dto;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;

import java.time.LocalDateTime;

public record TokenInfo(
        String tokenId,
        String userId,
        Integer priority,
        TokenStatus status,
        LocalDateTime tokenIssuedAt,
        LocalDateTime activeAt,
        LocalDateTime expireAt
){
    public static TokenInfo from(Token token) {
        return new TokenInfo(
                token.getTokenId(),
                token.getUserId(),
                0,
                token.getStatus(),
                token.getTokenIssuedAt(),
                token.getActiveAt(),
                token.getExpiredAt()
        );
    }

    public static TokenInfo from(TokenInfo info, int priority) {
        return new TokenInfo(
                info.tokenId(),
                info.userId(),
                priority,
                info.status(),
                info.tokenIssuedAt(),
                info.activeAt(),
                info.expireAt()
        );
    }
}
