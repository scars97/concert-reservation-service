package com.hhconcert.server.interfaces.facade;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.infrastructure.concert.ConcertJpaRepository;
import com.hhconcert.server.infrastructure.schedule.ScheduleJpaRepository;
import com.hhconcert.server.infrastructure.seat.SeatJpaRepository;
import com.hhconcert.server.infrastructure.user.UserJpaRepository;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationRequest;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("test")
@SpringBootTest
@Sql("classpath:test-data.sql")
class ReservationFacadeTest {

    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;
    @Autowired
    private ScheduleJpaRepository scheduleJpaRepository;
    @Autowired
    private SeatJpaRepository seatJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    LocalDate nowDate;
    LocalDateTime nowTime;

    @BeforeEach
    void setUp() {
        nowDate = LocalDate.now();
        nowTime = LocalDateTime.now().withNano(0);

        Concert saveConcert1 = concertJpaRepository.save(new Concert("콘서트1", nowDate, nowDate.plusDays(1)));
        Schedule saveSchedule1 = scheduleJpaRepository.save(new Schedule(saveConcert1, nowDate));
        Seat seat1 = seatJpaRepository.save(new Seat(saveSchedule1, "A1", 75000));
        User user1 = userJpaRepository.save(new User("test1234", 80000));
    }

    @DisplayName("좌석 임시 예약에 성공한다.")
    @Test
    void tempReserve() {
        ReservationResponse response = reservationFacade.tempReserve(new ReservationRequest("test1234", 1L, 1L, 1L));

        assertThat(response)
                .extracting("reserveId", "schedule", "seatNumber", "price", "status")
                .containsExactly(1L, nowDate, "A1", 75000, ReservationStatus.TEMP);
        assertThat(response.concert().id()).isEqualTo(1L);
        assertThat(response.expiredAt()).isNotNull();
        assertThat(response.expiredAt()).isAfter(response.createdAt());
    }

}