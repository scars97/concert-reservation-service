package com.hhconcert.server.business.domain.queues.persistance;

import com.hhconcert.server.business.domain.queues.entity.TokenVO;
import com.hhconcert.server.infrastructure.persistence.redis.TokenRedisRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Disabled("강제적으로 예외를 발생시켜야 동작하는 테스트")
class RedisTransactionalRollBackTest {

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private TokenRedisRepository tokenRedisRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    final String waitTokenKey = "wait-token";
    final String userId = "user1";

    @AfterEach
    void tearDown() {
        redisTemplate.delete(List.of(waitTokenKey, userId));
    }

    @DisplayName("조합된 Redis 명령어가 실행되는 경우, 예외가 발생하면 정상적으로 롤백된다.")
    @Test
    void whenCombinedRedisCommandExecuted_andThrowException_thenRolledBack() {
        TokenVO waitToken = TokenVO.createForWait(userId);

        Assertions.assertThrows(RuntimeException.class, () -> tokenRepository.addWaitToken(waitToken));

        Long tokenCount = tokenRepository.getRank(userId);
        assertThat(tokenCount).isNull();
        assertThatThrownBy(() -> tokenRepository.findTokenBy(userId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("등록되지 않은 토큰입니다.");
    }

    @DisplayName("Redis 명령어가 하나만 실행되는 경우, 예외가 발생하면 정상적으로 롤백된다.")
    @Test
    void whenRedisCommandExecuted_andThrowException_thenRolledBack() {
        Assertions.assertThrows(RuntimeException.class, () -> tokenRedisRepository.addWaitTokenForZset(userId, System.currentTimeMillis()));

        Long tokenCount = tokenRepository.getRank(userId);
        assertThat(tokenCount).isNull();
    }

}