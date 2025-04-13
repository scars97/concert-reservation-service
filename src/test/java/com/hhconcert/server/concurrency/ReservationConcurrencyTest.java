package com.hhconcert.server.concurrency;

import com.hhconcert.server.application.facade.ReservationFacade;
import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.domain.concert.entity.Schedule;
import com.hhconcert.server.business.domain.concert.entity.Seat;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.config.IntegrationTestSupport;
import com.hhconcert.server.infrastructure.persistence.jpa.ConcertJpaRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.ScheduleJpaRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.SeatJpaRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.UserJpaRepository;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

public class ReservationConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    private ReservationFacade reservationFacade;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ScheduleJpaRepository scheduleJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    Concert concert;
    Schedule schedule;
    Seat seat;

    @BeforeEach
    void setUp() {
        LocalDate nowDate = LocalDate.now();
        concert = concertJpaRepository.save(new Concert("콘서트1", nowDate, nowDate.plusDays(1)));
        schedule = scheduleJpaRepository.save(new Schedule(concert, nowDate));
        seat = seatJpaRepository.save(new Seat(schedule, "A1", 75000));
    }

    @DisplayName("같은 좌석에 대해 동시에 100명이 예약 요청하는 경우, 1명만 성공하고 나머지는 실패한다.")
    @Test
    void when10UsersReserveSeat_then1IsSuccessAndOtherIsFail() throws InterruptedException {
        int totalUsers = 100;
        for (int i = 0; i < totalUsers; i++) {
            String userId = "test" + (i + 1);
            userJpaRepository.save(new User(userId, 80000));
        }

        ExecutorService executor = Executors.newFixedThreadPool(totalUsers);
        CountDownLatch latch = new CountDownLatch(totalUsers);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < totalUsers; i++) {
            String userId = "test" + (i + 1);

            executor.submit(() -> {
                try {
                    reservationFacade.tempReserve(
                            new ReservationRequest(userId, concert.getId(), schedule.getId(), seat.getId()));
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertThat(successCount.get()).isOne();
        assertThat(failureCount.get()).isEqualTo(99);

        List<Reservation> reserve = reservationRepository.findReserveBySeatId(seat.getId());
        assertThat(reserve).hasSize(1);
    }
    
}
