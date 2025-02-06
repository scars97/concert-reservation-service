package com.hhconcert.server.infrastructure.queues;

import com.hhconcert.server.business.domain.queues.entity.TokenVO;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Repository
@RequiredArgsConstructor
public class TokenCoreRepositoryImpl implements TokenRepository {

    private final TokenRedisRepository redisRepository;

    @Override
    public void addWaitToken(TokenVO waitToken) {
        redisRepository.addTokenForValue(waitToken);
        redisRepository.addWaitTokenForZset(waitToken.userId(), System.currentTimeMillis());
    }

    @Override
    @Transactional
    public void addActiveToken(String userId) {
        TokenVO activeToken = TokenVO.updateForActive(redisRepository.findTokenAtValue(userId));

        redisRepository.addTokenForValue(activeToken);
        redisRepository.addActiveTokenForZset(userId, System.currentTimeMillis());
    }

    @Override
    public TokenVO findTokenBy(String userId) {
        return redisRepository.findTokenAtValue(userId);
    }

    @Override
    public Long getRank(String userId) {
        return redisRepository.getRank(userId);
    }

    @Override
    public Long getCountForActiveTokens() {
        return redisRepository.getCountForActiveTokens();
    }

    @Override
    public Long getCountForWaitTokens() {
        return redisRepository.getCountForWaitTokens();
    }

    @Override
    public Set<String> getWaitTokensToActivate(long activationCount) {
        return redisRepository.getWaitTokensAfterPop(activationCount);
    }

    @Override
    public void dropExpiredTokens(long currentTime) {
        redisRepository.dropExpiredTokens(currentTime);
    }

    @Override
    public void dropTokenByUserId(String userId) {
        redisRepository.dropActiveTokenForValue(userId);
        redisRepository.dropActiveTokenForZset(userId);
    }
}
