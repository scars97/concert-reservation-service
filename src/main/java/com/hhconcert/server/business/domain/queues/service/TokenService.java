package com.hhconcert.server.business.domain.queues.service;

import com.hhconcert.server.business.domain.queues.dto.TokenResult;
import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.global.exception.TokenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

        int activeCount = tokenRepository.getTokenCountFor(TokenStatus.ACTIVE);
        if (activeCount < MAX_ACTIVE_TOKEN_COUNT) {
            return TokenResult.from(tokenRepository.createToken(Token.createForActive(userId)));
        }

        int waitingCount = tokenRepository.getTokenCountFor(TokenStatus.WAIT);
        return TokenResult.from(tokenRepository.createToken(Token.createForWait(userId)), waitingCount + 1);
    }

    @Transactional
    public TokenResult checkQueueStatus(String userId) {
        Token token = tokenRepository.findTokenByUserId(userId);

        int currentPriority = 0;

        // WAIT 상태인 경우, 대기 순서 연산
        if (token.getStatus() == TokenStatus.WAIT) {
            List<Token> waitTokens = tokenRepository.getTokensBy(TokenStatus.WAIT);
            currentPriority = (int) waitTokens.stream()
                    .filter(t -> t.getCreateAt().isBefore(token.getCreateAt()))
                    .count() + 1;
        }

        return TokenResult.from(token, currentPriority);
    }

}
