package com.hhconcert.server.infrastructure.queues;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class TokenCoreRepositoryImpl implements TokenRepository {

    private final TokenJpaRepository repository;

    @Override
    public boolean isDuplicate(String userId) {
        return repository.existsByUserId(userId);
    }

    @Override
    public Token createToken(Token token) {
        return repository.save(token);
    }

    @Override
    public Integer nextPriority() {
        return repository.nextPriority();
    }

    @Override
    public Token findNextTokenToActivate() {
        return repository.findNextTokenToActivate();
    }

    @Override
    public Token findToken(String tokenId) {
        return repository.findById(tokenId).orElseThrow(() -> new NoSuchElementException("등록되지 않은 토큰입니다."));
    }

    @Override
    public int getTokensFor(TokenStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public void updateWaitingTokensPriority(Integer currentPriority) {
        repository.updateWaitingTokensPriority(currentPriority);
    }

    @Override
    public List<Token> getExpiredTokens(LocalDateTime now) {
        return repository.getExpiredTokens(now);
    }

    @Override
    public void dropToken(Token token) {
        repository.delete(token);
    }
}
