package com.hhconcert.server.business.domain.queues.service;

import com.hhconcert.server.business.domain.queues.dto.TokenResult;
import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenGenerator;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.infrastructure.queues.TokenJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class TokenServiceIntegrateTest {

    @Autowired
    private TokenService tokenService;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenJpaRepository tokenJpaRepository;

    private final static int MAX_ACTIVE_TOKEN_COUNT = 10;
    String testTokenId;

    @BeforeEach
    void setUp() {
        String userId = "test1234";
        testTokenId = TokenGenerator.generateToken(userId);
        tokenRepository.createToken(new Token(testTokenId, userId, TokenStatus.WAIT, 1));
    }

    @AfterEach
    void tearDown() {
        tokenJpaRepository.deleteAllInBatch();
    }

    @DisplayName("사용자 순서 도달 시, 토큰 상태가 ACTIVE로 변경된다.")
    @Test
    void checkQueueStatus() {
        TokenResult result = tokenService.checkQueueStatus(testTokenId);

        assertThat(result.status()).isEqualTo(TokenStatus.ACTIVE);
        assertThat(result.activeAt()).isBefore(result.expireAt());
        assertThat(result.expireAt()).isAfter(result.activeAt());
    }

    @DisplayName("대기열 활성화 인원이 모두 찬 경우, 토큰 상태는 변경되지 않는다.")
    @Test
    void whenQueueIsFull_thenDoNoting() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        for (int i = 0; i < MAX_ACTIVE_TOKEN_COUNT; i++) {
            String userId = "test" + i;
            String tokenId = TokenGenerator.generateToken(userId);
            tokenRepository.createToken(new Token(tokenId, userId, TokenStatus.ACTIVE, 0, now, now.plusMinutes(5)));
        }

        TokenResult result = tokenService.checkQueueStatus(testTokenId);

        assertThat(result.status()).isEqualTo(TokenStatus.WAIT);
        assertThat(result.activeAt()).isNull();
        assertThat(result.expireAt()).isNull();
    }


}