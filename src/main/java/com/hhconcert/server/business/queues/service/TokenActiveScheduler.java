package com.hhconcert.server.business.queues.service;

import com.hhconcert.server.business.queues.persistance.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@EnableScheduling
@Component
@RequiredArgsConstructor
public class TokenActiveScheduler {

    private final static int MAX_ACTIVE_TOKEN_COUNT = 1000;
    private final TokenRepository tokenRepository;

    @Scheduled(cron = "*/3 * * * * *") // 30초마다 실행
    public void activateTokens() {
        long activeCount = tokenRepository.getCountForActiveTokens();
        if (activeCount >= MAX_ACTIVE_TOKEN_COUNT) {
            return;
        }

        long availableCount = MAX_ACTIVE_TOKEN_COUNT - activeCount;
        long waitCount = tokenRepository.getCountForWaitTokens();

        if (availableCount >= waitCount) {
            availableCount = waitCount;
        }

        Set<String> activationTokens = tokenRepository.getWaitTokensToActivate(availableCount);
        activationTokens.forEach(tokenRepository::addActiveToken);

        log.info("Token Active : {}", activationTokens.size());
    }

}
