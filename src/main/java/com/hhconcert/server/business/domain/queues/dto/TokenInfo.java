package com.hhconcert.server.business.domain.queues.dto;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;

import java.time.LocalDateTime;

public record TokenInfo(
        String tokenId,
        String userId,
        Integer priority,
        TokenStatus status,
        LocalDateTime createAt,
        LocalDateTime activeAt,
        LocalDateTime expireAt
){
    public static TokenInfo from(Token token) {
        return new TokenInfo(
                token.getTokenId(),
                token.getUserId(),
                0,
                token.getStatus(),
                token.getCreatedAt(),
                token.getActiveAt(),
                token.getExpiredAt()
        );
    }
    public static TokenInfo from(Token token, int priority) {
        return new TokenInfo(
                token.getTokenId(),
                token.getUserId(),
                priority,
                token.getStatus(),
                token.getCreatedAt(),
                token.getActiveAt(),
                token.getExpiredAt()
        );
    }
}
