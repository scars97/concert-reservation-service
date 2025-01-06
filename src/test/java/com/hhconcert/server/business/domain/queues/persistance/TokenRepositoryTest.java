package com.hhconcert.server.business.domain.queues.persistance;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@Transactional
@SpringBootTest
class TokenRepositoryTest {

    @Autowired
    private TokenRepository repository;

    @DisplayName("토큰 생성")
    @Test
    void createToken() {
        String tokenId = UUID.randomUUID().toString();
        Token token = Token.builder()
                .tokenId(tokenId)
                .userId("new1234")
                .status(TokenStatus.WAIT)
                .priority(1)
                .build();
        Token saveToken = repository.createToken(token);

        assertThat(saveToken)
                .extracting("tokenId", "userId", "status", "priority")
                .containsExactly(tokenId, "new1234", TokenStatus.WAIT, 1);
        assertThat(saveToken.getCreateAt()).isNotNull();
    }

    @DisplayName("WAIT 상태 토큰의 대기 순서가 채번된다.")
    @Test
    void findNextPriority() {
        repository.createToken(new Token(UUID.randomUUID().toString(), "test1234", TokenStatus.WAIT, 1));
        repository.createToken(new Token(UUID.randomUUID().toString(), "qwer1234", TokenStatus.WAIT, 2));
        repository.createToken(new Token(UUID.randomUUID().toString(), "asdf1234", TokenStatus.WAIT, 3));

        Integer lastPriority = repository.nextPriority();

        assertThat(lastPriority).isEqualTo(4);
    }

    @DisplayName("우선순위가 가장 높은 WAIT 상태의 토큰이 조회된다.")
    @Test
    void findNextTokenToActivate() {
        repository.createToken(new Token(UUID.randomUUID().toString(), "test1234", TokenStatus.WAIT, 1));
        repository.createToken(new Token(UUID.randomUUID().toString(), "qwer1234", TokenStatus.WAIT, 2));
        repository.createToken(new Token(UUID.randomUUID().toString(), "asdf1234", TokenStatus.WAIT, 3));

        Token nextToken = repository.findNextTokenToActivate();

        assertThat(nextToken.getPriority()).isOne();
    }

    @DisplayName("토큰 상태가 ACTIVE인 토큰 목록을 조회한다.")
    @Test
    void getActiveTokens() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        String tokenId1 = UUID.randomUUID().toString();
        String tokenId2 = UUID.randomUUID().toString();
        String tokenId3 = UUID.randomUUID().toString();

        repository.createToken(new Token(tokenId1, "test1234", TokenStatus.WAIT, 1));
        repository.createToken(new Token(tokenId2, "qwer1234", TokenStatus.ACTIVE, 0, now, now.plusMinutes(5)));
        repository.createToken(new Token(tokenId3, "asdf1234", TokenStatus.ACTIVE, 0, now, now.plusMinutes(5)));

        int activeCount = repository.getTokensFor(TokenStatus.ACTIVE);

        assertThat(activeCount).isEqualTo(2);
    }

    @DisplayName("만료된 ACTIVE 상태의 토큰을 조회한다.")
    @Test
    void getExpiredTokens() {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        String tokenId1 = UUID.randomUUID().toString();
        String tokenId2 = UUID.randomUUID().toString();
        String tokenId3 = UUID.randomUUID().toString();
        repository.createToken(new Token(tokenId1, "test1234", TokenStatus.WAIT, 1));
        repository.createToken(new Token(tokenId2, "qwer1234", TokenStatus.ACTIVE, 0, now.minusMinutes(4), now.minusMinutes(1)));
        repository.createToken(new Token(tokenId3, "asdf1234", TokenStatus.ACTIVE, 0, now, now.plusMinutes(5)));

        List<Token> expiredTokens = repository.getExpiredTokens(now);

        assertThat(expiredTokens).hasSize(1)
                .extracting("tokenId", "status")
                .containsExactly(
                        tuple(tokenId2, TokenStatus.ACTIVE)
                );
    }

    @DisplayName("토큰의 대기 순번이 수정 된다.")
    @Test
    void updateWaitingTokensPriority() {
        String tokenId1 = UUID.randomUUID().toString();
        String tokenId2 = UUID.randomUUID().toString();
        String tokenId3 = UUID.randomUUID().toString();
        repository.createToken(new Token(tokenId1, "test1234", TokenStatus.WAIT, 2));
        repository.createToken(new Token(tokenId2, "qwer1234", TokenStatus.WAIT, 3));
        repository.createToken(new Token(tokenId3, "asdf1234", TokenStatus.WAIT, 4));

        repository.updateWaitingTokensPriority(1);

        Token findToken1 = repository.findToken(tokenId1);
        Token findToken2 = repository.findToken(tokenId2);
        Token findToken3 = repository.findToken(tokenId3);
        assertThat(findToken1.getPriority()).isOne();
        assertThat(findToken2.getPriority()).isEqualTo(2);
        assertThat(findToken3.getPriority()).isEqualTo(3);
    }
}