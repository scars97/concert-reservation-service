package com.hhconcert.server.interfaces.facade;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.payment.entity.PaymentStatus;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.infrastructure.concert.ConcertJpaRepository;
import com.hhconcert.server.infrastructure.reservation.ReservationJpaRepository;
import com.hhconcert.server.infrastructure.schedule.ScheduleJpaRepository;
import com.hhconcert.server.infrastructure.seat.SeatJpaRepository;
import com.hhconcert.server.infrastructure.user.UserJpaRepository;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentRequest;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentResponse;
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
class PaymentFacadeTest {

    @Autowired
    private PaymentFacade paymentFacade;

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

    LocalDate nowDate;
    LocalDateTime nowTime;

    @BeforeEach
    void setUp() {
        nowDate = LocalDate.now();
        nowTime = LocalDateTime.now().withNano(0);

        Concert saveConcert1 = concertJpaRepository.save(new Concert("콘서트1", nowDate, nowDate.plusDays(1)));
        Concert saveConcert2 = concertJpaRepository.save(new Concert("콘서트2", nowDate.plusDays(1), nowDate.plusDays(2)));

        Schedule saveSchedule1 = scheduleJpaRepository.save(new Schedule(saveConcert1, nowDate));
        Schedule saveSchedule2 = scheduleJpaRepository.save(new Schedule(saveConcert1, nowDate.plusDays(1)));

        Seat seat1 = seatJpaRepository.save(new Seat(saveSchedule1, "A1", 75000));
        Seat seat2 = seatJpaRepository.save(new Seat(saveSchedule1, "B1", 60000));
        Seat seat3 = seatJpaRepository.save(new Seat(saveSchedule1, "C1", 5000));

        User user1 = userJpaRepository.save(new User("test1234", 80000));
        User user2 = userJpaRepository.save(new User("qwer1234", 60000));
        User user3 = userJpaRepository.save(new User("asdf1234", 40000));

        Reservation saveReserve1 = reservationJpaRepository.save(new Reservation(user1, saveConcert1, saveSchedule1, seat1, seat1.getPrice(), ReservationStatus.TEMP, nowTime.plusMinutes(5)));
    }

    @DisplayName("예약건에 대한 결제가 완료된다.")
    @Test
    void payment() {
        PaymentResponse response = paymentFacade.payment(new PaymentRequest("test1234", 1L, 75000));

        assertThat(response)
                .extracting("paymentId", "reserveId", "userId", "price", "status")
                .containsExactly(1L, 1L, "test1234", 75000, PaymentStatus.SUCCESS);
    }

}