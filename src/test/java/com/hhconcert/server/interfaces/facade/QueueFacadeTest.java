package com.hhconcert.server.interfaces.facade;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.infrastructure.queues.TokenJpaRepository;
import com.hhconcert.server.infrastructure.user.UserJpaRepository;
import com.hhconcert.server.interfaces.api.queues.dto.TokenRequest;
import com.hhconcert.server.interfaces.api.queues.dto.TokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Sql("classpath:test-data.sql")
class QueueFacadeTest {

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
                TokenResponse response = queueFacade.createToken(new TokenRequest("test10"));

                assertThat(response.tokenId()).isEqualTo(UUID.nameUUIDFromBytes("test10".getBytes()).toString());
                assertThat(response.priority()).isZero();
                assertThat(response.status()).isEqualTo(TokenStatus.ACTIVE);
                assertThat(response.activeAt()).isBefore(response.expireAt());
                assertThat(response.expireAt()).isAfter(response.activeAt());
            }),
            DynamicTest.dynamicTest("활성화 인원이 10명 이상인 경우, WAIT 상태의 토큰이 생성된다.", () -> {
                TokenResponse response = queueFacade.createToken(new TokenRequest("test11"));

                assertThat(response.tokenId()).isEqualTo(UUID.nameUUIDFromBytes("test11".getBytes()).toString());
                assertThat(response.priority()).isOne();
                assertThat(response.status()).isEqualTo(TokenStatus.WAIT);
            })
        );
    }

    /*@DisplayName("대기열 상태 요청 시, WAIT 상태인 경우 대기 순서가 연산되어 반환된다.")
    @Test
    void checkQueueStatus() {
        LocalDateTime now = LocalDateTime.now().withNano(0);

        Token targetToken = Token.createForWait("target");
        Token token2 = Token.createForWait("qwer1");
        Token token3 = Token.createForWait("qwer2");
        Token token4 = Token.createForWait("qwer3");
        targetToken.setCreatedAt(now);
        token2.setCreatedAt(now.minusMinutes(1));
        token3.setCreatedAt(now.minusMinutes(2));
        token4.setCreatedAt(now.minusMinutes(3));

        tokenJpaRepository.saveAll(List.of(targetToken, token2, token3, token4));

        TokenResponse response = queueFacade.checkQueueStatus("target");

        assertThat(response.tokenId()).isEqualTo(UUID.nameUUIDFromBytes("target".getBytes()).toString());
        assertThat(response.priority()).isEqualTo(4);
        assertThat(response.status()).isEqualTo(TokenStatus.WAIT);
    }*/
}