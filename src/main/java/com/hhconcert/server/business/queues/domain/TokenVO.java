package com.hhconcert.server.business.queues.domain;

import java.time.LocalDateTime;

public record TokenVO (
        String tokenId,
        String userId,
        TokenStatus status,
        LocalDateTime tokenIssuedAt,
        LocalDateTime activeAt,
        LocalDateTime expiredAt
){
    public static TokenVO createForWait(String userId) {
        return new TokenVO(
                TokenGenerator.generateToken(userId),
                userId,
                TokenStatus.WAIT,
                LocalDateTime.now(),
                null,
                null
        );
    }

    public static TokenVO updateForActive(TokenVO token) {
        return new TokenVO(
                token.tokenId(),
                token.userId(),
                TokenStatus.ACTIVE,
                token.tokenIssuedAt(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(5)
        );
    }
}
