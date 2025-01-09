package com.hhconcert.server.interfaces.facade;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.payment.entity.Payment;
import com.hhconcert.server.business.domain.payment.entity.PaymentStatus;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.infrastructure.concert.ConcertJpaRepository;
import com.hhconcert.server.infrastructure.payment.PaymentJpaRepository;
import com.hhconcert.server.infrastructure.reservation.ReservationJpaRepository;
import com.hhconcert.server.infrastructure.schedule.ScheduleJpaRepository;
import com.hhconcert.server.infrastructure.seat.SeatJpaRepository;
import com.hhconcert.server.infrastructure.user.UserJpaRepository;
import com.hhconcert.server.interfaces.api.concert.dto.ConcertResponse;
import com.hhconcert.server.interfaces.api.concert.dto.ConcertScheduleResponse;
import com.hhconcert.server.interfaces.api.concert.dto.ScheduleSeatResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

@ActiveProfiles("test")
@SpringBootTest
@Sql("classpath:test-data.sql")
class ConcertFacadeTest {

    @Autowired
    private ConcertFacade concertFacade;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;
    @Autowired
    private ScheduleJpaRepository scheduleJpaRepository;
    @Autowired
    private SeatJpaRepository seatJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ReservationJpaRepository reservationJpaRepository;
    @Autowired
    private PaymentJpaRepository paymentJpaRepository;

    LocalDate nowDate;
    LocalDateTime nowTime;

    @BeforeEach
    void setUp() {
        nowDate = LocalDate.now();
        nowTime = LocalDateTime.now().withNano(0);

        Concert saveConcert1 = concertJpaRepository.save(new Concert("콘서트1", nowDate, nowDate.plusDays(1)));
        Concert saveConcert2 = concertJpaRepository.save(new Concert("콘서트2", nowDate.plusDays(1), nowDate.plusDays(2)));

        Schedule saveSchedule1 = scheduleJpaRepository.save(new Schedule(saveConcert1,nowDate));
        Schedule saveSchedule2 = scheduleJpaRepository.save(new Schedule(saveConcert1,nowDate.plusDays(1)));

        Seat seat1 = seatJpaRepository.save(new Seat(saveSchedule1, "A1", 75000));
        Seat seat2 = seatJpaRepository.save(new Seat(saveSchedule1, "B1", 60000));
        Seat seat3 = seatJpaRepository.save(new Seat(saveSchedule1, "C1", 5000));

        User user1 = userJpaRepository.save(new User("test1234", 80000));
        User user2 = userJpaRepository.save(new User("qwer1234", 60000));
        User user3 = userJpaRepository.save(new User("asdf1234", 40000));

        Reservation saveReserve1 = reservationJpaRepository.save(new Reservation(user1, saveConcert1, saveSchedule1, seat1, seat1.getPrice(), ReservationStatus.TEMP, nowTime.plusMinutes(5)));
        Reservation saveReserve2 = reservationJpaRepository.save(new Reservation(user1, saveConcert1, saveSchedule1, seat1, seat1.getPrice(), ReservationStatus.CANCEL, nowTime.minusMinutes(6)));
        Reservation saveReserve3 = reservationJpaRepository.save(new Reservation(user2, saveConcert1, saveSchedule1, seat2, seat2.getPrice(), ReservationStatus.TEMP, nowTime.minusMinutes(3)));
        Reservation saveReserve4 = reservationJpaRepository.save(new Reservation(user3, saveConcert1, saveSchedule1, seat3, seat3.getPrice(), ReservationStatus.COMPLETE, nowTime.minusMinutes(2)));

        paymentJpaRepository.save(new Payment(user1, saveReserve2, seat1.getPrice(), PaymentStatus.CANCEL));
        paymentJpaRepository.save(new Payment(user3, saveReserve4, seat3.getPrice(), PaymentStatus.SUCCESS));
    }

    @DisplayName("콘서트 목록을 조회한다.")
    @Test
    void getConcerts() {
        List<ConcertResponse> responses = concertFacade.getConcerts();

        assertThat(responses).extracting("id", "title", "startDate", "endDate")
                .containsExactly(
                        tuple(1L, "콘서트1", nowDate, nowDate.plusDays(1)),
                        tuple(2L, "콘서트2", nowDate.plusDays(1), nowDate.plusDays(2))
                );
    }

    @DisplayName("단일 콘서트를 조회한다.")
    @Test
    void findConcert() {
        ConcertResponse response = concertFacade.findConcert(1L);

        assertThat(response).extracting("id", "title", "startDate", "endDate")
                .containsExactly(1L, "콘서트1", nowDate, nowDate.plusDays(1));
    }

    @DisplayName("특정 콘서트의 예약 날짜를 조회한다.")
    @Test
    void getSchedules() {
        ConcertScheduleResponse response = concertFacade.getSchedules(1L);

        assertThat(response.schedules()).extracting("scheduleId", "date")
                .containsExactly(
                        tuple(1L, nowDate),
                        tuple(2L, nowDate.plusDays(1))
                );
    }

    @DisplayName("예약 가능 좌석을 조회한다.")
    @Test
    void getAvailableSeats() {
        ScheduleSeatResponse response = concertFacade.getAvailableSeats(1L);

        assertThat(response.seats()).extracting("seatId", "seatNumber" , "price")
                .containsExactly(
                        tuple(2L, "B1", 60000)
                );
    }

}