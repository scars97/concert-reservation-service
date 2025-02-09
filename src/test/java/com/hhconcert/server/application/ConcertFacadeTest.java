package com.hhconcert.server.application;

import com.hhconcert.server.application.dto.ConcertResult;
import com.hhconcert.server.application.dto.ScheduleResult;
import com.hhconcert.server.application.dto.SeatResult;
import com.hhconcert.server.application.facade.ConcertFacade;
import com.hhconcert.server.config.IntegrationTestSupport;
import com.hhconcert.server.interfaces.api.concert.dto.ConcertRequest;
import com.hhconcert.server.interfaces.api.concert.dto.ScheduleRequest;
import com.hhconcert.server.fixture.FacadeTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ConcertFacadeTest extends IntegrationTestSupport {

    @Autowired
    private ConcertFacade concertFacade;

    @Autowired
    private FacadeTestFixture fixture;

    LocalDate nowDate;
    LocalDateTime nowTime;

    @BeforeEach
    void setUp() {
        nowDate = LocalDate.now();
        nowTime = LocalDateTime.now().withNano(0);
        long currentTimeMills = System.currentTimeMillis();

        fixture.concertFixture(nowDate, nowTime, currentTimeMills);
    }

    @DisplayName("콘서트 목록을 조회한다.")
    @Test
    void getConcerts() {
        List<ConcertResult> results = concertFacade.getConcerts();

        assertThat(results).extracting("id", "title", "startDate", "endDate")
                .containsExactly(
                        tuple(1L, "콘서트1", nowDate, nowDate.plusDays(1)),
                        tuple(2L, "콘서트2", nowDate.plusDays(1), nowDate.plusDays(2))
                );
    }

    @DisplayName("단일 콘서트를 조회한다.")
    @Test
    void findConcert() {
        ConcertResult result = concertFacade.findConcert(new ConcertRequest(1L));

        assertThat(result).extracting("id", "title", "startDate", "endDate")
                .containsExactly(1L, "콘서트1", nowDate, nowDate.plusDays(1));
    }

    @DisplayName("특정 콘서트의 예약 날짜를 조회한다.")
    @Test
    void getSchedules() {
        List<ScheduleResult> results = concertFacade.getSchedules(new ConcertRequest(1L));

        assertThat(results).extracting("scheduleId", "date")
                .containsExactly(
                        tuple(1L, nowDate),
                        tuple(2L, nowDate.plusDays(1))
                );
    }

    @DisplayName("예약 가능 좌석을 조회한다.")
    @Test
    void getAvailableSeats() {
        List<SeatResult> results = concertFacade.getAvailableSeats(new ScheduleRequest(1L));

        assertThat(results).extracting("seatId", "seatNumber" , "price")
                .containsExactly(
                        tuple(2L, "B1", 60000)
                );
    }

}