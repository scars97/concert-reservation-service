package com.hhconcert.server.business.domain.queues;

import com.hhconcert.server.business.queues.domain.TokenStatus;
import com.hhconcert.server.business.queues.domain.TokenVO;
import com.hhconcert.server.business.queues.persistance.TokenRepository;
import com.hhconcert.server.business.queues.service.TokenActiveScheduler;
import com.hhconcert.server.config.IntegrationTestSupport;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class TokenActiveSchedulerTest extends IntegrationTestSupport {

    @Autowired
    private TokenActiveScheduler tokenActiveScheduler;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.delete(List.of("wait-token", "active-token", "user1", "user2"));
    }

    @DisplayName("WAIT 상태 토큰들을 활성화할 수 있는 만큼 ACTIVE 상태로 변경한다.")
    @Test
    void whenPassedQueue_thenUpdateStatusIsActivate() {
        tokenRepository.addWaitToken(TokenVO.createForWait("user1"));
        tokenRepository.addWaitToken(TokenVO.createForWait("user2"));

        tokenActiveScheduler.activateTokens();

        assertThat(tokenRepository.getRank("user1")).isNull();
        assertThat(tokenRepository.getRank("user2")).isNull();

        Long activeCount = tokenRepository.getCountForActiveTokens();
        assertThat(activeCount).isEqualTo(2);

        List<TokenVO> activeTokens = List.of(
            tokenRepository.findTokenBy("user1"),
            tokenRepository.findTokenBy("user2")
        );
        assertThat(activeTokens).hasSize(2)
                .extracting("status")
                .contains(TokenStatus.ACTIVE);
    }

}