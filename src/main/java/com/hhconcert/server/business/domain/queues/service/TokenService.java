package com.hhconcert.server.business.domain.queues.service;

import com.hhconcert.server.business.domain.queues.dto.TokenResult;
import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.global.exception.TokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final static int MAX_ACTIVE_TOKEN_COUNT = 10;
    private final TokenRepository tokenRepository;

    @Transactional
    public TokenResult createToken(String userId) {
        if (tokenRepository.isDuplicate(userId)) {
            throw new TokenException("이미 토큰이 존재합니다.");
        }

        int activeCount = tokenRepository.getTokensFor(TokenStatus.ACTIVE);

        Token token = activeCount < MAX_ACTIVE_TOKEN_COUNT ?
                Token.createForActive(userId) :
                Token.createForWait(userId, tokenRepository.nextPriority());

        return TokenResult.from(tokenRepository.createToken(token));
    }

    @Transactional
    public TokenResult checkQueueStatus(String tokenId) {
        Token token = tokenRepository.findToken(tokenId);
        if (token.getStatus() != TokenStatus.WAIT) {
            throw new TokenException("유효하지 않은 토큰입니다.");
        }

        int activeCount = tokenRepository.getTokensFor(TokenStatus.ACTIVE);

        if (activeCount < MAX_ACTIVE_TOKEN_COUNT) {
            token.activeForMinutes(5);
        }

        return TokenResult.from(token);
    }

    @Transactional
    public void expireToken(String tokenId) {
        Token token = tokenRepository.findToken(tokenId);
        if (token.getStatus() != TokenStatus.ACTIVE) {
            throw new TokenException("유효하지 않은 토큰입니다.");
        }

        tokenRepository.dropToken(token);
    }
}
