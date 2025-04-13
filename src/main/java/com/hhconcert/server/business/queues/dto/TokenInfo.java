package com.hhconcert.server.business.queues.dto;

import com.hhconcert.server.business.queues.domain.TokenStatus;
import com.hhconcert.server.business.queues.domain.TokenVO;

import java.time.LocalDateTime;

public record TokenInfo(
        String tokenId,
        String userId,
        Long priority,
        TokenStatus status,
        LocalDateTime tokenIssuedAt,
        LocalDateTime activeAt,
        LocalDateTime expireAt
){
    public static TokenInfo from(TokenVO token) {
        return new TokenInfo(
                token.tokenId(),
                token.userId(),
                0L,
                token.status(),
                token.tokenIssuedAt(),
                token.activeAt(),
                token.expiredAt()
        );
    }

    public static TokenInfo from(TokenInfo info, long priority) {
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
