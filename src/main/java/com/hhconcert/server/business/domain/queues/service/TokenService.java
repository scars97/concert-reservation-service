package com.hhconcert.server.business.domain.queues.service;

import com.hhconcert.server.business.domain.queues.dto.TokenInfo;
import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.global.common.error.ErrorCode;
import com.hhconcert.server.global.common.exception.BusinessException;
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
            throw new BusinessException(ErrorCode.DUPLICATED_TOKEN);
        }

        Token waitToken = Token.createForWait(userId);

        return TokenInfo.from(tokenRepository.createToken(waitToken));
    }

    @Transactional
    public TokenInfo checkQueueStatus(TokenInfo info) {
        int currentPriority = 0;

        // WAIT 상태인 경우, 대기 순서 연산
        if (info.status() == TokenStatus.WAIT) {
            List<Token> waitTokens = tokenRepository.getTokensBy(TokenStatus.WAIT);
            currentPriority = (int) waitTokens.stream()
                    .filter(t -> t.getTokenIssuedAt().isBefore(info.tokenIssuedAt()))
                    .count() + 1;
        }

        return TokenInfo.from(info, currentPriority);
    }

    public TokenInfo findTokenBy(String userId) {
        return TokenInfo.from(tokenRepository.findTokenByUserId(userId));
    }

}
