package com.hhconcert.server.business.domain.queues.service;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
class TokenActiveSchedulerTest {

    @Autowired
    private TokenActiveScheduler tokenActiveScheduler;

    @Autowired
    private TokenRepository tokenRepository;

    @DisplayName("대기열 통과 시, WAIT 상태 토큰 1개를 ACTIVE 상태로 변경한다.")
    @Test
    void whenPassedQueue_thenUpdateStatusIsActivate() {
        Token token1 = tokenRepository.createToken(Token.createForWait("test1", 1));
        Token token2 = tokenRepository.createToken(Token.createForWait("test2", 2));

        tokenActiveScheduler.activateTokens();

        Token findToken1 = tokenRepository.findToken(token1.getTokenId());
        assertThat(findToken1.getStatus()).isEqualTo(TokenStatus.ACTIVE);

        Token findToken2 = tokenRepository.findToken(token2.getTokenId());
        assertThat(findToken2).extracting("status", "priority")
                        .containsExactly(TokenStatus.WAIT, 1);
    }

}