package com.hhconcert.server.infrastructure.queues;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    public List<Token> findNextTokensToActivate(TokenStatus status, int limit) {
        PageRequest pageable = PageRequest.of(0, limit);
        return repository.findByStatusOrderByCreatedAtAsc(status, pageable);
    }

    @Override
    public Token findToken(String tokenId) {
        return repository.findById(tokenId).orElseThrow(() -> new NoSuchElementException("등록되지 않은 토큰입니다."));
    }

    @Override
    public Token findTokenByUserId(String userId) {
        return repository.findByUserId(userId).orElseThrow(() -> new NoSuchElementException("등록되지 않은 토큰입니다."));
    }

    @Override
    public int getTokenCountFor(TokenStatus status) {
        return repository.countByStatus(status);
    }

    @Override
    public List<Token> getTokensBy(TokenStatus status) {
        return repository.findByStatus(status);
    }

    @Override
    public void dropExpiredTokens(LocalDateTime now) {
        repository.deleteByExpiredAtBefore(now);
    }

    @Override
    public void dropTokenByUserId(String userId) {
        repository.deleteByUserId(userId);
    }
}
