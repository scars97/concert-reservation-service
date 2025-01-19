package com.hhconcert.server.application;

import com.hhconcert.server.application.dto.TokenResult;
import com.hhconcert.server.application.facade.QueueFacade;
import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenGenerator;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.config.IntegrationTestSupport;
import com.hhconcert.server.infrastructure.queues.TokenJpaRepository;
import com.hhconcert.server.infrastructure.user.UserJpaRepository;
import com.hhconcert.server.interfaces.api.queues.dto.TokenRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class QueueFacadeTest extends IntegrationTestSupport {

    @Autowired
    private QueueFacade queueFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private TokenJpaRepository tokenJpaRepository;

    @BeforeEach
    void setUp() {
        // 사용자 11명 추가
        for (int i = 0; i < 11; i++) {
            String userId = "test" + (i + 1);
            userJpaRepository.save(new User(userId, 10000));
        }

        // 활성화 인원 9명
        for (int i = 0; i < 9; i++) {
            String userId = "test" + (i + 1);
            tokenJpaRepository.save(Token.createForActive(userId));
        }
    }

    @DisplayName("활성화 인원에 따라 상태 값이 다른 토큰이 생성된다.")
    @TestFactory
    Collection<DynamicTest> createToken() {
        return List.of(
            DynamicTest.dynamicTest("활성화 인원이 10명 미만인 경우, ACTIVE 상태의 토큰이 생성된다.", () -> {
                TokenResult result = queueFacade.createToken(new TokenRequest("test10"));

                assertThat(result.tokenId()).isEqualTo(UUID.nameUUIDFromBytes("test10".getBytes()).toString());
                assertThat(result.priority()).isZero();
                assertThat(result.status()).isEqualTo(TokenStatus.ACTIVE);
                assertThat(result.activeAt()).isBefore(result.expireAt());
                assertThat(result.expireAt()).isAfter(result.activeAt());
            }),
            DynamicTest.dynamicTest("활성화 인원이 10명 이상인 경우, WAIT 상태의 토큰이 생성된다.", () -> {
                TokenResult result = queueFacade.createToken(new TokenRequest("test11"));

                assertThat(result.tokenId()).isEqualTo(UUID.nameUUIDFromBytes("test11".getBytes()).toString());
                assertThat(result.priority()).isOne();
                assertThat(result.status()).isEqualTo(TokenStatus.WAIT);
            })
        );
    }

    @DisplayName("대기열 상태 요청 시, WAIT 상태인 경우 대기 순서가 연산되어 반환된다.")
    @Test
    void checkQueueStatus() {
        LocalDateTime now = LocalDateTime.now();

        Token targetToken = new Token(TokenGenerator.generateToken("target"), "target", TokenStatus.WAIT, null, null, now);
        Token token1 = new Token(TokenGenerator.generateToken("qwer1"), "qwer1", TokenStatus.WAIT, null, null, now.minusMinutes(1));
        Token token2 = new Token(TokenGenerator.generateToken("qwer2"), "qwer2", TokenStatus.WAIT, null, null, now.minusMinutes(2));
        Token token3 = new Token(TokenGenerator.generateToken("qwer3"), "qwer3", TokenStatus.WAIT, null, null, now.minusMinutes(3));
        tokenJpaRepository.saveAll(List.of(targetToken, token1, token2, token3));

        TokenResult result = queueFacade.checkQueueStatus(new TokenRequest("target"));

        assertThat(result).extracting("priority", "status")
                        .containsExactly(4, TokenStatus.WAIT);
    }
}