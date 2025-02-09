package com.hhconcert.server.infrastructure.queues;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.entity.TokenVO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TokenRedisRepository {

    private static final String WAIT_TOKEN_KEY = "wait-token";
    private static final String ACTIVE_TOKEN_KEY = "active-token";

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public void addWaitTokenForZset(String userId, long currentTime) {
        redisTemplate.opsForZSet().add(WAIT_TOKEN_KEY, userId, currentTime);
    }

    public void addActiveTokenForZset(String userId, long currentTime) {
        redisTemplate.opsForZSet().add(ACTIVE_TOKEN_KEY, userId, currentTime);
    }

    public void addTokenForValue(TokenVO token) {
        String jsonData;
        try {
            jsonData = objectMapper.writeValueAsString(token);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Serialisation failure", e);
        }

        if (token.status() == TokenStatus.WAIT) {
            redisTemplate.opsForValue().set(token.userId(), jsonData);
        } else {
            redisTemplate.opsForValue().set(token.userId(), jsonData, 5L, TimeUnit.MINUTES);
        }
    }

    public TokenVO findTokenAtValue(String userId) {
        var tokenData = redisTemplate.opsForValue().get(userId);

        if (tokenData == null) {
            throw new NoSuchElementException("등록되지 않은 토큰입니다.");
        }

        try {
            return objectMapper.readValue(tokenData.toString(), objectMapper.getTypeFactory().constructType(TokenVO.class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Deserialisation failure", e);
        }
    }

    public Long getRank(String userId) {
        return redisTemplate.opsForZSet().rank(WAIT_TOKEN_KEY, userId);
    }

    public Long getCountForActiveTokens() {
        return redisTemplate.opsForZSet().zCard(ACTIVE_TOKEN_KEY);
    }

    public Long getCountForWaitTokens() {
        return redisTemplate.opsForZSet().zCard(WAIT_TOKEN_KEY);
    }

    public Set<String> getWaitTokensAfterPop(long activationCount) {
        // TODO @Transactional 설정 시, NPE 발생
        return redisTemplate.opsForZSet().popMin(WAIT_TOKEN_KEY, activationCount)
                .stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .map(String::valueOf)
                .collect(Collectors.toSet());
    }

    public void dropExpiredTokens(Long currentTime) {
        redisTemplate.opsForZSet().removeRangeByScore(ACTIVE_TOKEN_KEY, 0, currentTime);
    }

    public void dropActiveTokenForValue(String userId) {
        redisTemplate.delete(userId);
    }

    public void dropActiveTokenForZset(String userId) {
        redisTemplate.opsForZSet().remove(ACTIVE_TOKEN_KEY, userId);
    }

}
