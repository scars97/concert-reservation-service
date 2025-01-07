package com.hhconcert.server.business.domain.queues.service;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenGenerator;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class TokenSchedulerTest {

    @Autowired
    private TokenScheduler tokenScheduler;

    @Autowired
    private TokenRepository tokenRepository;

    @DisplayName("대기열 통과 시, WAIT 상태 토큰 1개를 ACTIVE 상태로 변경한다.")
    @Test
    void whenPassedQueue_thenUpdateStatusIsActivate() {
        Token token1 = tokenRepository.createToken(Token.createForWait("test1", 1));
        Token token2 = tokenRepository.createToken(Token.createForWait("test2", 2));

        tokenScheduler.activateTokens();

        Token findToken1 = tokenRepository.findToken(token1.getTokenId());
        assertThat(findToken1.getStatus()).isEqualTo(TokenStatus.ACTIVE);

        Token findToken2 = tokenRepository.findToken(token2.getTokenId());
        assertThat(findToken2).extracting("status", "priority")
                        .containsExactly(TokenStatus.WAIT, 1);
    }

    @DisplayName("만료된 ACTIVE 상태의 토큰을 삭제한다.")
    @Test
    void dropTokens() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Token token1 = tokenRepository.createToken(new Token(TokenGenerator.generateToken("test1"), "test1",
                TokenStatus.ACTIVE, 0, now.minusMinutes(7), now.minusMinutes(2)));
        Token token2 = tokenRepository.createToken(new Token(TokenGenerator.generateToken("test2"), "test2",
                TokenStatus.ACTIVE, 0, now.minusMinutes(2), now.plusMinutes(3)));

        tokenScheduler.dropTokens();

        assertThatThrownBy(() -> tokenRepository.findToken(token1.getTokenId()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("등록되지 않은 토큰입니다.");

        Token dropToken2 = tokenRepository.findToken(token2.getTokenId());
        assertThat(dropToken2).isNotNull();
    }
}