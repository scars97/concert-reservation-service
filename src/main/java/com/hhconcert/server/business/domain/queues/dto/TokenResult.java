package com.hhconcert.server.business.domain.queues.dto;

import com.hhconcert.server.business.domain.queues.entity.Token;
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
){
    public static TokenResult from(Token token) {
        return new TokenResult(
                token.getTokenId(),
                token.getUserId(),
                0,
                token.getStatus(),
                token.getCreateAt(),
                token.getActiveAt(),
                token.getExpiredAt()
        );
    }
    public static TokenResult from(Token token, int priority) {
        return new TokenResult(
                token.getTokenId(),
                token.getUserId(),
                priority,
                token.getStatus(),
                token.getCreateAt(),
                token.getActiveAt(),
                token.getExpiredAt()
        );
    }
}
