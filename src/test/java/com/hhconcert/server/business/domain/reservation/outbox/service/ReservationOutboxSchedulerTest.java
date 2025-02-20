package com.hhconcert.server.business.domain.reservation.outbox.service;

import com.hhconcert.server.application.event.reservation.ReserveSuccessEvent;
import com.hhconcert.server.business.domain.reservation.outbox.entity.ReservationOutbox;
import com.hhconcert.server.business.domain.reservation.outbox.repository.ReservationOutboxRepository;
import com.hhconcert.server.config.IntegrationTestSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationOutboxSchedulerTest extends IntegrationTestSupport {

    @Autowired
    private ReservationOutboxScheduler outboxScheduler;

    @Autowired
    private ReservationOutboxRepository outboxRepository;

    @BeforeEach
    void setUp() {
        LocalDate nowDate = LocalDate.now();
        LocalDateTime fiveMinuteAgo = LocalDateTime.now().minusMinutes(5);

        ReserveSuccessEvent failEvent1 = new ReserveSuccessEvent(1L, "user1", "A 콘서트",
                nowDate.toString(), "A1", 75000, fiveMinuteAgo.toString());
        ReserveSuccessEvent failEvent2 = new ReserveSuccessEvent(2L, "user2", "B 콘서트",
                nowDate.toString(), "B1", 60000, fiveMinuteAgo.toString());

        ReservationOutbox outbox1 = ReservationOutbox.init(1L);
        outbox1.setPublishedAt(LocalDateTime.now().minusMinutes(5));
        ReservationOutbox outbox2 = ReservationOutbox.init(2L);
        outbox2.setPublishedAt(LocalDateTime.now().minusMinutes(5));

        outbox1.toPayload(failEvent1);
        outbox2.toPayload(failEvent2);

        outboxRepository.save(outbox1);
        outboxRepository.save(outbox2);
    }

    @DisplayName("5분 이상된 INIT 상태의 Outbox 이벤트를 재발행한다.")
    @Test
    void rePublished_ShouldResendInitOutboxEventsOlderThanFiveMinutes() {
        // when
        outboxScheduler.rePublished();

        Awaitility.await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> {
                List<ReservationOutbox> outboxes = outboxRepository.findInitOutboxOlderThan(5);
                assertThat(outboxes.size()).isZero();
            });
    }
}