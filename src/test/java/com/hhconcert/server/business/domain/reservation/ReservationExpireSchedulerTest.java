package com.hhconcert.server.business.domain.reservation;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.domain.reservation.service.ReservationExpireScheduler;
import com.hhconcert.server.business.domain.concert.entity.Schedule;
import com.hhconcert.server.business.domain.concert.entity.Seat;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.config.IntegrationTestSupport;
import com.hhconcert.server.infrastructure.persistence.jpa.ConcertJpaRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.ReservationJpaRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.ScheduleJpaRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.SeatJpaRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class ReservationExpireSchedulerTest extends IntegrationTestSupport {

    @Autowired
    private ReservationExpireScheduler reservationExpireScheduler;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ScheduleJpaRepository scheduleJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @BeforeEach
    void setUp() {
        LocalDate nowDate = LocalDate.now();
        Concert concert = concertJpaRepository.save(new Concert("콘서트1", nowDate, nowDate.plusDays(1)));
        Schedule schedule = scheduleJpaRepository.save(new Schedule(concert, nowDate));
        Seat seat1 = seatJpaRepository.save(new Seat(schedule, "A1", 75000));
        Seat seat2 = seatJpaRepository.save(new Seat(schedule, "B1", 60000));
        Seat seat3 = seatJpaRepository.save(new Seat(schedule, "C1", 50000));

        User user1 = userJpaRepository.save(new User("test1234", 80000));
        User user2 = userJpaRepository.save(new User("qwer1234", 60000));
        User user3 = userJpaRepository.save(new User("asdf1234", 40000));

        LocalDateTime nowTime = LocalDateTime.now();
        reservationRepository.createTempReserve(new Reservation(user1, concert, schedule, seat1, seat1.getPrice(), ReservationStatus.TEMP, nowTime.minusMinutes(1)));
        reservationRepository.createTempReserve(new Reservation(user2, concert, schedule, seat2, seat2.getPrice(), ReservationStatus.TEMP, nowTime.minusMinutes(2)));
        reservationRepository.createTempReserve(new Reservation(user3, concert, schedule, seat3, seat3.getPrice(), ReservationStatus.TEMP, nowTime.plusMinutes(3)));

        redisTemplate.delete("reserved-seats");
    }

    @DisplayName("임시 예약 시간이 만료된 경우, 예약 상태가 CANCEL로 변경된다.")
    @Test
    void whenExpireReserveTime_thenStatusModifiedToCancel() {
        long currentTime = System.currentTimeMillis();
        reservationRepository.addReservedSeatId(1L, currentTime - 100);
        reservationRepository.addReservedSeatId(2L, currentTime - 200);
        reservationRepository.addReservedSeatId(3L, currentTime + 300);

        reservationExpireScheduler.expireReservedSeats();

        Set<Long> reservedSeats = reservationRepository.getReservedSeatIds();
        assertThat(reservedSeats).hasSize(1).contains(3L);

        List<Reservation> reservations = reservationJpaRepository.findAll();
        assertThat(reservations).hasSize(3)
                .extracting("id", "status")
                .containsExactly(
                    tuple(1L, ReservationStatus.CANCEL),
                    tuple(2L, ReservationStatus.CANCEL),
                    tuple(3L, ReservationStatus.TEMP)
                );
    }

}