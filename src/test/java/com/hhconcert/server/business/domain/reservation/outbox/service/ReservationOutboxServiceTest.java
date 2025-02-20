package com.hhconcert.server.business.domain.reservation.outbox.service;

import com.hhconcert.server.application.event.reservation.ReserveSuccessEvent;
import com.hhconcert.server.business.domain.reservation.outbox.entity.OutboxStatus;
import com.hhconcert.server.business.domain.reservation.outbox.entity.ReservationOutbox;
import com.hhconcert.server.business.domain.reservation.outbox.repository.ReservationOutboxRepository;
import com.hhconcert.server.config.IntegrationTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationOutboxServiceTest extends IntegrationTestSupport {

    @Autowired
    private ReservationOutboxService outboxService;

    @Autowired
    private ReservationOutboxRepository outboxRepository;

    @DisplayName("예약에 대한 Outbox 가 생성된다.")
    @Test
    void init() {
        // given
        ReserveSuccessEvent event = new ReserveSuccessEvent(1L, "user1", "A 콘서트",
                LocalDate.now().toString(), "A1", 75000, LocalDateTime.now().toString());

        // when
        outboxService.init(event);

        // then
        ReservationOutbox findOutbox = outboxRepository.findByReserveId(1L);
        assertThat(findOutbox).extracting("id", "status", "reserveId")
                .containsExactly(1L, OutboxStatus.INIT, 1L);
        assertThat(findOutbox.getPayload()).isNotNull();
    }

    @DisplayName("Outbox 상태가 INIT 에서 PUBLISHED 로 변경된다.")
    @Test
    void updateForPublished() {
        // given
        ReserveSuccessEvent event = new ReserveSuccessEvent(1L, "user1", "A 콘서트",
                LocalDate.now().toString(), "A1", 75000, LocalDateTime.now().toString());

        ReservationOutbox outbox = ReservationOutbox.init(1L);
        outbox.toPayload(event);
        outboxRepository.save(outbox);

        // when
        outboxService.updateForPublished(event);

        // then
        ReservationOutbox findOutbox = outboxRepository.findByReserveId(1L);
        assertThat(findOutbox).extracting("id", "status", "reserveId")
                .containsExactly(1L, OutboxStatus.PUBLISHED, 1L);
    }

}