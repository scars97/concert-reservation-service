package com.hhconcert.server.business.domain.queues;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenTest {

    @DisplayName("WAIT 상태의 토큰을 생성한다.")
    @Test
    void createTokenForWait() {
        String userId = "test1234";

        Token token = Token.createForWait(userId);

        assertThat(token.getTokenId()).isNotNull();
        assertThat(token)
                .extracting("userId", "status")
                .containsExactly(userId, TokenStatus.WAIT);
    }


    @DisplayName("ACTIVE 상태의 토큰을 생성한다.")
    @Test
    void createTokenForActive() {
        String userId = "test1234";

        Token token = Token.createForActive(userId);

        assertThat(token.getTokenId()).isNotNull();
        assertThat(token.getUserId()).isEqualTo(userId);
        assertThat(token.getUserId()).isEqualTo(userId);
        assertThat(token.getStatus()).isEqualTo(TokenStatus.ACTIVE);
        assertThat(token.getActiveAt()).isBefore(token.getExpiredAt());
        assertThat(token.getExpiredAt()).isAfter(token.getActiveAt());
    }

    @DisplayName("토큰을 ACTIVE 상태로 수정 시, 활성화/만료 시간이 추가된다.")
    @Test
    void updateTokenStatus_withActiveTimeAndExpireTime() {
        String userId = "test1234";
        Token token = Token.createForWait(userId);

        token.activeForMinutes(5);

        assertThat(token.getStatus()).isEqualTo(TokenStatus.ACTIVE);
        assertThat(token.getActiveAt()).isBefore(token.getExpiredAt());
        assertThat(token.getExpiredAt()).isAfter(token.getActiveAt());
    }
}