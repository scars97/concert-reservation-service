package com.hhconcert.server.business.domain.queues.service;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@EnableScheduling
@Component
@RequiredArgsConstructor
public class TokenActiveScheduler {

    private final static int MAX_ACTIVE_TOKEN_COUNT = 10;
    private final TokenRepository tokenRepository;

    @Scheduled(cron = "*/30 * * * * *") // 30초마다 실행
    @Transactional
    public void activateTokens() {
        int activeCount = tokenRepository.getTokenCountFor(TokenStatus.ACTIVE);
        if (activeCount >= MAX_ACTIVE_TOKEN_COUNT) {
            return;
        }

        Token nextToken = tokenRepository.findNextTokenToActivate(TokenStatus.WAIT);
        if (nextToken != null) {
            nextToken.activeForMinutes(5);
            log.info("update active token : {}", nextToken.getUserId());
        }
    }

}
