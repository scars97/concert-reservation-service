package com.hhconcert.server.business.domain.queues;

import com.hhconcert.server.business.domain.queues.entity.TokenGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenGeneratorTest {

    @DisplayName("사용자 ID 정보가 포함된 토큰 ID가 생성된다.")
    @Test
    void generateToken() {
        String userId = "user1";
        String tokenId = TokenGenerator.generateToken(userId);

        assertThat(tokenId).isNotNull();
    }

    @DisplayName("토큰 ID 에서 사용자 ID 정보를 복원한다.")
    @Test
    void decodedTokenIdToUserId() {
        String originalUserId = "user1";
        String tokenId = TokenGenerator.generateToken(originalUserId);

        String restoreUserId = TokenGenerator.tokenIdToUserId(tokenId);

        assertThat(restoreUserId).isEqualTo(originalUserId);
    }

}