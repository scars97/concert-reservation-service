package com.hhconcert.server.business.domain.queues;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.business.domain.queues.service.TokenActiveScheduler;
import com.hhconcert.server.config.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TokenActiveSchedulerTest extends IntegrationTestSupport {

    @Autowired
    private TokenActiveScheduler tokenActiveScheduler;

    @Autowired
    private TokenRepository tokenRepository;

    @DisplayName("WAIT 상태 토큰들을 활성화할 수 있는 만큼 ACTIVE 상태로 변경한다.")
    @Test
    void whenPassedQueue_thenUpdateStatusIsActivate() {
        tokenRepository.createToken(Token.createForWait("test1"));
        tokenRepository.createToken(Token.createForWait("test2"));

        tokenActiveScheduler.activateTokens();

        List<Token> tokens = tokenRepository.getTokensBy(TokenStatus.ACTIVE);
        assertThat(tokens).hasSize(2)
                .extracting("status")
                .contains(TokenStatus.ACTIVE);
    }

}