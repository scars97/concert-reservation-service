package com.hhconcert.server.application;

import com.hhconcert.server.application.dto.TokenResult;
import com.hhconcert.server.application.facade.QueueFacade;
import com.hhconcert.server.business.domain.queues.entity.TokenGenerator;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.entity.TokenVO;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.config.IntegrationTestSupport;
import com.hhconcert.server.infrastructure.persistence.jpa.UserJpaRepository;
import com.hhconcert.server.interfaces.api.queues.dto.TokenRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class QueueFacadeTest extends IntegrationTestSupport {

    @Autowired
    private QueueFacade queueFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    final String waitTokenKey = "wait-token";
    final String userId = "user1";

    @AfterEach
    void tearDown() {
        redisTemplate.delete(List.of(waitTokenKey, userId));
    }

    @DisplayName("토큰 발급 요청 시, WAIT 상태의 토큰이 발급된다.")
    @Test
    void createToken() {
        userJpaRepository.save(new User(userId, 10000));

        TokenResult result = queueFacade.createToken(new TokenRequest("user1"));

        assertThat(result.tokenId()).isNotNull();
        assertThat(result.priority()).isOne();
        assertThat(result.status()).isEqualTo(TokenStatus.WAIT);
    }

    @DisplayName("대기열 상태 요청 시, WAIT 상태인 경우 대기 순서가 연산되어 반환된다.")
    @Test
    void checkQueueStatus() {
        TokenVO targetToken = new TokenVO(TokenGenerator.generateToken(userId), userId, TokenStatus.WAIT, LocalDateTime.now(), null, null);
        tokenRepository.addWaitToken(targetToken);

        TokenResult result = queueFacade.checkQueueStatus(new TokenRequest(userId));

        assertThat(result).extracting("priority", "status")
                        .containsExactly(1L, TokenStatus.WAIT);
    }

}