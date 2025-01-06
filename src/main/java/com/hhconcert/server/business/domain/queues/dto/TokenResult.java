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
                token.getPriority(),
                token.getStatus(),
                token.getCreateAt(),
                token.getActiveAt(),
                token.getExpiredAt()
        );
    }
}
