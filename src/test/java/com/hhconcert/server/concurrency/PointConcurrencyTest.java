package com.hhconcert.server.concurrency;

import com.hhconcert.server.application.dto.PointResult;
import com.hhconcert.server.application.facade.UserPointFacade;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.config.IntegrationTestSupport;
import com.hhconcert.server.infrastructure.persistence.jpa.UserJpaRepository;
import com.hhconcert.server.interfaces.api.point.dto.PointRequest;
import com.hhconcert.server.interfaces.api.point.dto.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class PointConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    private UserPointFacade userPointFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        userJpaRepository.save(new User("test1234", 0));
    }

    // 낙관적 락 적용
    @DisplayName("동시에 여러 번 충전 요청이 들어오는 경우, 한 번만 충전되어야 한다.")
    @Test
    void whenMultipleChargingRequests_thenOnlyOneCharged() {
        int totalRequest = 10;

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < totalRequest; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    userPointFacade.chargePoint(new PointRequest("test1234", 1000));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
            });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        assertThat(successCount.get()).isOne();
        assertThat(failureCount.get()).isEqualTo(9);

        PointResult userPoint = userPointFacade.getPoint(new UserRequest("test1234"));
        assertThat(userPoint).extracting("userId", "point")
                .containsExactly("test1234", 1000);
    }

    // 비관적 락 적용
    /*@DisplayName("동시에 n번 충전 요청이 들어오는 경우, n번 모두 충전된다.")
    @Test
    void whenMultipleChargingRequests_thenAllCharged() {
        int totalRequest = 10;

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        List<CompletableFuture<Void>> futures = new ArrayList<>();
        for (int i = 0; i < totalRequest; i++) {
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                try {
                    userPointFacade.chargePoint(new PointRequest("test1234", 1000));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                }
            });
            futures.add(future);
        }

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        assertThat(successCount.get()).isEqualTo(10);
        assertThat(failureCount.get()).isZero();

        PointResult userPoint = userPointFacade.getPoint(new UserRequest("test1234"));
        assertThat(userPoint).extracting("userId", "point")
                .containsExactly("test1234", 10000);
    }*/

}
