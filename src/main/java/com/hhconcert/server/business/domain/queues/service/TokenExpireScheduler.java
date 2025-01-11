package com.hhconcert.server.business.domain.queues.service;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@EnableScheduling
@Component
@RequiredArgsConstructor
public class TokenExpireScheduler {

    private final TokenRepository tokenRepository;

    @Scheduled(cron = "0 * * * * *") // 1분마다 실행
    @Transactional
    public void dropTokens() {
        List<Token> expiredTokens = tokenRepository.getExpiredTokens(LocalDateTime.now());
        for (Token token : expiredTokens) {
            tokenRepository.dropToken(token);
        }
        log.info("remove expired tokens : {}", expiredTokens.size());
    }
}
