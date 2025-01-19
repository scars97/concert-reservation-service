package com.hhconcert.server.application;

import com.hhconcert.server.application.dto.ReservationResult;
import com.hhconcert.server.application.facade.ReservationFacade;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.config.IntegrationTestSupport;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationRequest;
import com.hhconcert.server.fixture.FacadeTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ReservationFacadeTest extends IntegrationTestSupport {

    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private FacadeTestFixture fixture;

    LocalDate nowDate;
    LocalDateTime nowTime;

    @BeforeEach
    void setUp() {
        nowDate = LocalDate.now();
        nowTime = LocalDateTime.now().withNano(0);

        fixture.reservationFixture(nowDate, nowTime);
    }

    @DisplayName("좌석 임시 예약에 성공한다.")
    @Test
    void tempReserve() {
        ReservationResult result = reservationFacade.tempReserve(new ReservationRequest("test1234", 1L, 1L, 1L));

        assertThat(result)
                .extracting("reserveId", "schedule", "seatNumber", "price", "status")
                .containsExactly(1L, nowDate, "A1", 75000, ReservationStatus.TEMP);
        assertThat(result.concert().id()).isEqualTo(1L);
        assertThat(result.expiredAt()).isNotNull();
        assertThat(result.expiredAt()).isAfter(result.createdAt());
    }

}