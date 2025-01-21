package com.hhconcert.server.concurrency;

import com.hhconcert.server.application.facade.QueueFacade;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.config.IntegrationTestSupport;
import com.hhconcert.server.infrastructure.user.UserJpaRepository;
import com.hhconcert.server.interfaces.api.queues.dto.TokenRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class TokenConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    private QueueFacade queueFacade;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            users.add(new User("test" + (i + 1), 20000));
        }

        userJpaRepository.saveAll(users);
    }

    @DisplayName("동시에 20명이 토큰을 생성하는 경우, 20명 모두 WAIT 상태 토큰이 발급된다.")
    @Test
    void createTokenAtTheSameTime_then10ActiveAnd10Wait() throws InterruptedException {
        int totalUsers = 20;
        ExecutorService executor = Executors.newFixedThreadPool(totalUsers);
        CountDownLatch latch = new CountDownLatch(totalUsers);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < totalUsers; i++) {
            String userId = "test" + (i + 1);

            executor.submit(() -> {
                try {
                    queueFacade.createToken(new TokenRequest(userId));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertThat(successCount.get()).isEqualTo(20);
        assertThat(failureCount.get()).isZero();

        int waitCount = tokenRepository.getTokenCountFor(TokenStatus.WAIT);
        assertThat(waitCount).isEqualTo(20);
    }

}
