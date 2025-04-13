package com.hhconcert.server.business.queues.service;

import com.hhconcert.server.business.queues.persistance.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class TokenExpireScheduler {

    private final TokenRepository tokenRepository;

    @Scheduled(cron = "0 * * * * *") // 1분마다 실행
    @Transactional
    public void dropTokens() {
        tokenRepository.dropExpiredTokens(System.currentTimeMillis());
    }
}
