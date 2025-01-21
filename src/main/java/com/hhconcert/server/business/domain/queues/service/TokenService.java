package com.hhconcert.server.business.domain.queues.service;

import com.hhconcert.server.business.domain.queues.dto.TokenInfo;
import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.exception.TokenErrorCode;
import com.hhconcert.server.business.domain.queues.exception.TokenException;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    @Transactional
    public TokenInfo createToken(String userId) {
        if (tokenRepository.isDuplicate(userId)) {
            throw new TokenException(TokenErrorCode.DUPLICATED_TOKEN);
        }

        Token waitToken = tokenRepository.createToken(Token.createForWait(userId));

        int waitCount = tokenRepository.getTokenCountFor(TokenStatus.WAIT);
        return TokenInfo.from(waitToken, waitCount);
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
