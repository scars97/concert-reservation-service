package com.hhconcert.server.business.domain.queues.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TokenGeneratorTest {

    @DisplayName("userId 정보가 포함된 토큰 ID가 생성된다.")
    @Test
    void generateToken() {
        String userId = "test1234";
        String tokenId = TokenGenerator.generateToken(userId);

        assertThat(tokenId).isNotNull();
        assertThat(tokenId).isEqualTo(TokenGenerator.generateToken("test1234"));
    }

}