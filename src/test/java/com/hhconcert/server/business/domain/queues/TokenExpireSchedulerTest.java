package com.hhconcert.server.business.domain.queues;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenGenerator;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.business.domain.queues.service.TokenExpireScheduler;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
class TokenExpireSchedulerTest {

    @Autowired
    private TokenExpireScheduler tokenExpireScheduler;

    @Autowired
    private TokenRepository tokenRepository;

    @DisplayName("만료된 ACTIVE 상태의 토큰을 삭제한다.")
    @Test
    void dropTokens() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Token token1 = tokenRepository.createToken(new Token(TokenGenerator.generateToken("test1"), "test1",
                TokenStatus.ACTIVE, now.minusMinutes(7), now.minusMinutes(2), now.minusMinutes(10)));
        Token token2 = tokenRepository.createToken(new Token(TokenGenerator.generateToken("test2"), "test2",
                TokenStatus.ACTIVE, now.minusMinutes(2), now.plusMinutes(3), now.minusMinutes(10)));

        tokenExpireScheduler.dropTokens();

        assertThatThrownBy(() -> tokenRepository.findToken(token1.getTokenId()))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("등록되지 않은 토큰입니다.");

        Token dropToken2 = tokenRepository.findToken(token2.getTokenId());
        assertThat(dropToken2).isNotNull();
    }

}