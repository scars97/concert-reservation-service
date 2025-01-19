package com.hhconcert.server.business.domain.queues.service;

import com.hhconcert.server.business.domain.queues.dto.TokenInfo;
import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.exception.TokenErrorCode;
import com.hhconcert.server.business.domain.queues.exception.TokenException;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final static int MAX_ACTIVE_TOKEN_COUNT = 10;
    private final TokenRepository tokenRepository;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public TokenInfo createToken(String userId) {
        if (tokenRepository.isDuplicate(userId)) {
            throw new TokenException(TokenErrorCode.DUPLICATED_TOKEN);
        }

        List<Token> activeTokens = tokenRepository.getTokensBy(TokenStatus.ACTIVE);
        if (activeTokens.size() < MAX_ACTIVE_TOKEN_COUNT) {
            Token token = tokenRepository.createToken(Token.createForActive(userId));
            return TokenInfo.from(token);
        }

        List<Token> waitTokens = tokenRepository.getTokensBy(TokenStatus.WAIT);
        Token waitToken = tokenRepository.createToken(Token.createForWait(userId));
        return TokenInfo.from(waitToken, waitTokens.size() + 1);
    }

    @Transactional
    public TokenInfo checkQueueStatus(String userId) {
        Token token = tokenRepository.findTokenByUserId(userId);

        int currentPriority = 0;

        // WAIT 상태인 경우, 대기 순서 연산
        if (token.getStatus() == TokenStatus.WAIT) {
            List<Token> waitTokens = tokenRepository.getTokensBy(TokenStatus.WAIT);
            currentPriority = (int) waitTokens.stream()
                    .filter(t -> t.getTokenIssuedAt().isBefore(token.getTokenIssuedAt()))
                    .count() + 1;
        }

        return TokenInfo.from(token, currentPriority);
    }

}
