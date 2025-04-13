package com.hhconcert.server.application;

import com.hhconcert.server.application.dto.ReservationResult;
import com.hhconcert.server.application.facade.ReservationFacade;
import com.hhconcert.server.business.reservation.domain.ReservationStatus;
import com.hhconcert.server.config.IntegrationTestSupport;
import com.hhconcert.server.fixture.FacadeTestFixture;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.Duration;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationFacadeTest extends IntegrationTestSupport {

    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private FacadeTestFixture fixture;

    LocalDate nowDate;

    @BeforeEach
    void setUp() {
        nowDate = LocalDate.now();

        fixture.reservationFixture(nowDate);
    }

    @DisplayName("좌석 임시 예약에 성공한다.")
    @Test
    void tempReserve() {
        ReservationResult result = reservationFacade.tempReserve(new ReservationRequest("test1234", 1L, 1L, 1L));

        Awaitility.await()
            .atMost(Duration.ofSeconds(10))
            .untilAsserted(() -> {
                assertThat(result)
                    .extracting("reserveId", "schedule", "seatNumber", "price", "status")
                    .containsExactly(1L, nowDate, "A1", 75000, ReservationStatus.TEMP);
                assertThat(result.concert().id()).isEqualTo(1L);
                assertThat(result.expiredAt()).isNotNull();
                assertThat(result.expiredAt()).isAfter(result.createdAt());
            });
    }

}