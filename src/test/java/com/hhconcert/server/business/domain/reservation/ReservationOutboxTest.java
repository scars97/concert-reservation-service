package com.hhconcert.server.business.domain.reservation;

import com.hhconcert.server.business.reservation.dto.ReserveSuccessEvent;
import com.hhconcert.server.business.reservation.domain.OutboxStatus;
import com.hhconcert.server.business.reservation.domain.ReservationOutbox;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationOutboxTest {

    @DisplayName("outbox 가 정상적으로 생성된다.")
    @Test
    void createOutbox() {
        Long reserveId = 1L;

        ReservationOutbox outbox = ReservationOutbox.init(reserveId);

        assertThat(outbox).extracting("reserveId", "status")
                .containsExactly(reserveId, OutboxStatus.INIT);
    }

    @DisplayName("outbox의 상태가 PUBLISHED 로 변경된다.")
    @Test
    void updateForPublished() {
        ReservationOutbox outbox = ReservationOutbox.init(1L);

        outbox.updateForPublished();

        assertThat(outbox.getStatus()).isEqualTo(OutboxStatus.PUBLISHED);
    }

    @DisplayName("payload 직렬화/역직렬화 테스트 시나리오")
    @TestFactory
    Collection<DynamicTest> serializationTest() {
        // given
        ReserveSuccessEvent event = new ReserveSuccessEvent(1L, "user1", "A 콘서트",
                LocalDate.now().toString(), "A1", 75000, LocalDateTime.now().toString());
        ReservationOutbox outbox = ReservationOutbox.init(1L);

        return List.of(
            DynamicTest.dynamicTest("payload 전환", () -> {
                // when
                outbox.toPayload(event);

                //then
                assertThat(outbox.getPayload()).isNotNull();
            }),
            DynamicTest.dynamicTest("ReservationSuccessEvent 전환", () -> {
                // when
                ReserveSuccessEvent result = outbox.toReserveSuccessEvent();

                // then
                assertThat(result).isEqualTo(event);
            })
        );
    }
}