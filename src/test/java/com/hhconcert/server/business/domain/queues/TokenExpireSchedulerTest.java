package com.hhconcert.server.business.domain.queues;

import com.hhconcert.server.business.queues.service.TokenExpireScheduler;
import com.hhconcert.server.config.IntegrationTestSupport;
import com.hhconcert.server.infrastructure.persistence.redis.TokenRedisRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenExpireSchedulerTest extends IntegrationTestSupport {

    @Autowired
    private TokenExpireScheduler tokenExpireScheduler;

    @Autowired
    private TokenRedisRepository tokenRedisRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void tearDown() {
        redisTemplate.delete(List.of("active-token", "user1", "user2"));
    }

    @DisplayName("만료된 ACTIVE 상태의 토큰을 삭제한다.")
    @Test
    void dropTokens() {
        long currentTime = System.currentTimeMillis();
        tokenRedisRepository.addActiveTokenForZset("user1", currentTime - 1);
        tokenRedisRepository.addActiveTokenForZset("user2", currentTime - 1);

        tokenExpireScheduler.dropTokens();

        Long activeCount = tokenRedisRepository.getCountForActiveTokens();
        assertThat(activeCount).isZero();
        assertTokenNotFound("user1");
        assertTokenNotFound("user2");
    }

    private void assertTokenNotFound(String userId) {
        assertThatThrownBy(() -> tokenRedisRepository.findTokenAtValue(userId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("등록되지 않은 토큰입니다.");
    }

}