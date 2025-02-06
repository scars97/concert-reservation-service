package com.hhconcert.server.business.domain.queues;

import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.entity.TokenVO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenVOTest {

    @DisplayName("WAIT 상태의 토큰을 생성한다.")
    @Test
    void createTokenForWait() {
        String userId = "user1";

        TokenVO token = TokenVO.createForWait(userId);

        assertThat(token.tokenId()).isNotNull();
        assertThat(token)
                .extracting("userId", "status")
                .containsExactly(userId, TokenStatus.WAIT);
    }

    @DisplayName("토큰을 ACTIVE 상태로 수정 시, 활성화/만료 시간이 추가된다.")
    @Test
    void updateTokenStatus_withActiveTimeAndExpireTime() {
        String userId = "user1";
        TokenVO waitToken = TokenVO.createForWait(userId);

        TokenVO activeToken = TokenVO.updateForActive(waitToken);

        assertThat(activeToken.status()).isEqualTo(TokenStatus.ACTIVE);
        assertThat(activeToken.activeAt()).isBefore(activeToken.expiredAt());
        assertThat(activeToken.expiredAt()).isAfter(activeToken.activeAt());
    }
}