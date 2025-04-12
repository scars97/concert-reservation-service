package com.hhconcert.server.business.domain.queues.service;

import com.hhconcert.server.business.domain.queues.dto.TokenInfo;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.entity.TokenVO;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.global.exception.ErrorCode;
import com.hhconcert.server.global.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    @Transactional
    public TokenInfo createToken(String userId) {
        if (tokenRepository.getRank(userId) != null) {
            throw new BusinessException(ErrorCode.DUPLICATED_TOKEN);
        }

        TokenVO waitToken = TokenVO.createForWait(userId);

        tokenRepository.addWaitToken(waitToken);

        return TokenInfo.from(waitToken);
    }

    public TokenInfo checkQueueStatus(TokenInfo info) {
        long currentPriority = info.status() == TokenStatus.WAIT ?
            tokenRepository.getRank(info.userId()) + 1 : 0;

        return TokenInfo.from(info, currentPriority);
    }

    public TokenInfo findTokenBy(String userId) {
        return TokenInfo.from(tokenRepository.findTokenBy(userId));
    }

    public void dropTokenBy(String userId) {
        tokenRepository.dropTokenByUserId(userId);
    }

}
