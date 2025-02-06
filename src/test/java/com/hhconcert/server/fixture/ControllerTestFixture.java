package com.hhconcert.server.fixture;

import com.hhconcert.server.application.dto.*;
import com.hhconcert.server.business.domain.payment.entity.PaymentStatus;
import com.hhconcert.server.business.domain.queues.entity.TokenGenerator;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ControllerTestFixture {

    public ConcertResult createConcert() {
        return new ConcertResult(1L, "콘서트 1", LocalDate.now(), LocalDate.now().plusDays(1));
    }

    public ScheduleResult createSchedule() {
        return new ScheduleResult(1L, createConcert(), LocalDate.now());
    }

    public SeatResult createSeat() {
        return new SeatResult(1L, "A1", 75000);
    }

    public TokenResult createWaitToken() {
        return new TokenResult(
                TokenGenerator.generateToken("test1234"),
                "test1234",
                1L,
                TokenStatus.WAIT,
                LocalDateTime.now(),
                null,
                null
        );
    }

    public PointResult createUserPoint(int amount) {
        return new PointResult("test1234", amount);
    }

    public ReservationResult createTempReserve(LocalDateTime now) {
        return new ReservationResult(
                1L,
                LocalDate.now(),
                "A1",
                createConcert(),
                75000,
                ReservationStatus.TEMP,
                now,
                now.plusMinutes(5)
        );
    }

    public PaymentResult createPayment() {
        LocalDateTime now = LocalDateTime.now();
        return new PaymentResult(
                1L,
                createTempReserve(now),
                new UserResult("test1234", 80000),
                75000,
                PaymentStatus.SUCCESS,
                now
        );
    }
}
