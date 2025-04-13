package com.hhconcert.server.interfaces.api.queues.dto;

import com.hhconcert.server.application.dto.TokenResult;
import com.hhconcert.server.business.queues.domain.TokenStatus;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record TokenResponse(
        @Schema(description = "토큰 ID",  example = "15e859ae-9bf2-4f08-ae68-465e9dcd54bf")
        String tokenId,
        @Schema(description = "사용자 ID",  example = "user1234")
        String userId,
        @Schema(description = "대기 순번",  example = "10")
        Long priority,
        @Schema(description = "토큰 상태",  example = "WAIT")
        TokenStatus status,
        @Schema(description = "토큰 생성 일시",  example = "2025-01-30T11:50:000Z")
        LocalDateTime tokenIssuedAt,
        @Schema(description = "토큰 활성화 일시",  example = "")
        LocalDateTime activeAt,
        @Schema(description = "토큰 만료 일시",  example = "")
        LocalDateTime expireAt
) {

    public static TokenResponse from(TokenResult result) {
        return new TokenResponse(
                result.tokenId(),
                result.userId(),
                result.priority(),
                result.status(),
                result.tokenIssuedAt(),
                result.activeAt(),
                result.expireAt()
        );
    }
}
