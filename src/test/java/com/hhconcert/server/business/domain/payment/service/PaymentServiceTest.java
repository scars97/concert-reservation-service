package com.hhconcert.server.business.domain.payment.service;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.payment.dto.PaymentCommand;
import com.hhconcert.server.business.domain.payment.dto.PaymentInfo;
import com.hhconcert.server.business.domain.payment.entity.Payment;
import com.hhconcert.server.business.domain.payment.entity.PaymentStatus;
import com.hhconcert.server.business.domain.payment.persistance.PaymentRepository;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.business.domain.user.persistance.UserRepository;
import com.hhconcert.server.business.domain.payment.exception.PaymentErrorCode;
import com.hhconcert.server.business.domain.payment.exception.PaymentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;
    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;

    User user;
    Concert concert;
    Schedule schedule;
    Seat seat;
    Reservation reservation;
    Payment payment;

    @BeforeEach
    void setUp() {
        user = new User("test1234", 80000);
        concert = new Concert(1L, "콘서트", LocalDate.now(), LocalDate.now().plusDays(1));
        schedule = new Schedule(1L, concert, LocalDate.now());
        seat = new Seat(1L, schedule, "A1", 75000);
        reservation = new Reservation(1L, user, concert, schedule, seat,
                75000, ReservationStatus.TEMP, LocalDateTime.now().plusMinutes(5));
        payment = new Payment(1L, user, reservation, 75000, PaymentStatus.SUCCESS);
    }

    @DisplayName("결제에 성공한다.")
    @Test
    void payment() {
        when(reservationRepository.findReserve(1L)).thenReturn(reservation);
        when(userRepository.findUser("test1234")).thenReturn(user);
        when(paymentRepository.payment(any(Payment.class))).thenReturn(payment);

        PaymentInfo result = paymentService.payment(new PaymentCommand("test1234", 1L, 75000));

        assertThat(result.paymentId()).isEqualTo(1L);
        assertThat(result.reserve().reserveId()).isEqualTo(1L);
        assertThat(result.user().point()).isEqualTo(5000);
        assertThat(result.price()).isEqualTo(75000);
        assertThat(result.status()).isEqualTo(PaymentStatus.SUCCESS);

        verify(tokenRepository, times(1)).dropTokenByUserId("test1234");
    }

    @DisplayName("결제 금액이 일치하지 않는 경우 예외가 발생한다.")
    @Test
    void whenInvalidAmount_thenThrowException() {
        when(reservationRepository.findReserve(1L)).thenReturn(reservation);

        assertThatThrownBy(() -> paymentService.payment(new PaymentCommand("test1234", 1L, 70000)))
                .isInstanceOf(PaymentException.class)
                .hasFieldOrPropertyWithValue("errorCode", PaymentErrorCode.NOT_MATCH_PAYMENT_AMOUNT)
                .extracting("errorCode")
                .extracting("status", "message")
                .containsExactly(HttpStatus.BAD_REQUEST, "결제 금액이 일치하지 않습니다.");
    }

}