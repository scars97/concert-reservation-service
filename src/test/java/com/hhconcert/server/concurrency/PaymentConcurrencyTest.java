package com.hhconcert.server.concurrency;

import com.hhconcert.server.application.facade.PaymentFacade;
import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.payment.entity.Payment;
import com.hhconcert.server.business.domain.payment.entity.PaymentStatus;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.config.IntegrationTestSupport;
import com.hhconcert.server.infrastructure.persistence.jpa.ConcertJpaRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.PaymentJpaRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.ReservationJpaRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.ScheduleJpaRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.SeatJpaRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.UserJpaRepository;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class PaymentConcurrencyTest extends IntegrationTestSupport {

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ReservationJpaRepository reservationJpaRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @Autowired
    private ScheduleJpaRepository scheduleJpaRepository;

    @Autowired
    private SeatJpaRepository seatJpaRepository;

    @Autowired
    private PaymentJpaRepository paymentJpaRepository;

    User user;
    Concert concert;
    Schedule schedule;
    Seat seat;
    Reservation reservation;

    @BeforeEach
    void setUp() {
        LocalDate nowDate = LocalDate.now();
        user = userJpaRepository.save(new User("test1234", 80000));
        concert = concertJpaRepository.save(new Concert("콘서트1", nowDate, nowDate.plusDays(1)));
        schedule = scheduleJpaRepository.save(new Schedule(concert, nowDate));
        seat = seatJpaRepository.save(new Seat(schedule, "A1", 75000));
        reservation = reservationJpaRepository.save(new Reservation(user, concert, schedule, seat, 75000, ReservationStatus.TEMP, LocalDateTime.now().plusMinutes(5)));
    }

    @DisplayName("한 예약건에 대해 동시에 결제 요청이 들어온 경우, 중복 결제가 발생하지 않는다.")
    @Test
    void whenPaymentRequestForReservation_thenNoDuplicatePayments() throws InterruptedException {
        int totalRequest = 5;

        ExecutorService executor = Executors.newFixedThreadPool(totalRequest);
        CountDownLatch latch = new CountDownLatch(totalRequest);

        AtomicInteger failureCount = new AtomicInteger();

        for (int i = 0; i < totalRequest; i++) {
            executor.submit(() -> {
                try {
                    paymentFacade.payment(new PaymentRequest("test1234", 1L, 75000));
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        assertThat(failureCount.get()).isEqualTo(4);

        List<Payment> payments = paymentJpaRepository.findAll();
        assertThat(payments).hasSize(1)
                .extracting("id", "user.id", "status")
                .containsExactly(
                        tuple(1L, 1L, PaymentStatus.SUCCESS)
                );
    }

}
