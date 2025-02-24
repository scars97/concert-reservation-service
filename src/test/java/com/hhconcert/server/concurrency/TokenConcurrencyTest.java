package com.hhconcert.server.concurrency;

import com.hhconcert.server.application.dto.TokenResult;
import com.hhconcert.server.application.facade.QueueFacade;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.config.IntegrationTestSupport;
import com.hhconcert.server.infrastructure.user.UserJpaRepository;
import com.hhconcert.server.interfaces.api.queues.dto.TokenRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

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
    private UserJpaRepository userJpaRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        int totalUser = 5;
        List<User> users = new ArrayList<>();
        for (int i = 0; i < totalUser; i++) {
            users.add(new User("test" + (i + 1), 20000));
        }
        userJpaRepository.saveAll(users);
    }

    @AfterEach
    void tearDown() {
        int totalUser = 5;
        for (int i = 0; i < totalUser; i++) {
            redisTemplate.delete("test" + (i + 1));
        }
        redisTemplate.delete("wait-token");
    }

    @DisplayName("동시에 5명이 토큰을 생성하는 경우, 토큰이 모두 생성되고 1 ~ 5 까지 대기 순서가 지정된다.")
    @Test
    void when5PeopleCreateTokenAtTheSametime_thenIssuedTokensAndAssignedQueueOrderFrom1To5() throws InterruptedException {
        int totalUsers = 5;
        ExecutorService executor = Executors.newFixedThreadPool(totalUsers);
        CountDownLatch latch = new CountDownLatch(totalUsers);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        List<TokenResult> results = new ArrayList<>();
        for (int i = 0; i < totalUsers; i++) {
            String userId = "test" + (i + 1);

            executor.submit(() -> {
                try {
                    Thread.sleep(300);
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

        assertThat(results).hasSize(5).extracting("priority")
                .contains(1L, 2L, 3L, 4L, 5L);
    }

}
