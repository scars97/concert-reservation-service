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
    public Token findNextTokenToActivate(TokenStatus status) {
        return repository.findFirstByStatusOrderByCreateAtAsc(status);
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
    public List<Token> getExpiredTokens(LocalDateTime now) {
        return repository.findByStatusAndExpiredAtLessThanEqual(TokenStatus.ACTIVE, now);
    }

    @Override
    public void dropToken(Token token) {
        repository.delete(token);
    }

    @Override
    public void dropTokenByUserId(String userId) {
        repository.deleteByUserId(userId);
    }
}
