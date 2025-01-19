package com.hhconcert.server.concurrency;

import com.hhconcert.server.application.dto.TokenResult;
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

    @DisplayName("동시에 20명이 토큰을 생성하는 경우, 10명은 ACTIVE, 나머지 10명은 WAIT 상태 토큰이 발급된다.")
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

        int activeCount = tokenRepository.getTokenCountFor(TokenStatus.ACTIVE);
        int waitCount = tokenRepository.getTokenCountFor(TokenStatus.WAIT);
        assertThat(activeCount).isEqualTo(10);
        assertThat(waitCount).isEqualTo(10);
    }

    @DisplayName("활성화 인원이 모두 차고 5명이 동시에 토큰을 생성하는 경우, 1 ~ 5 대기 순서가 반환된다.")
    @Test
    void whenActiveTokensFull_thenWaitTokensCreatedWithSequentialOrder() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            String userId = "test" + (i + 1);
            queueFacade.createToken(new TokenRequest(userId));
        }

        int totalUsers = 5;
        ExecutorService executor = Executors.newFixedThreadPool(totalUsers);
        CountDownLatch latch = new CountDownLatch(totalUsers);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        List<TokenResult> results = new ArrayList<>();
        for (int i = 0; i < totalUsers; i++) {
            String userId = "test" + (i + 11);

            executor.submit(() -> {
                try {
                    results.add(queueFacade.createToken(new TokenRequest(userId)));
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

        assertThat(successCount.get()).isEqualTo(5);
        assertThat(failureCount.get()).isZero();

        assertThat(results).hasSize(5)
                .extracting("priority")
                .contains(1, 2, 3, 4, 5);
    }

}
