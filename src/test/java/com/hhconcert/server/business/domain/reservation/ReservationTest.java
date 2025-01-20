package com.hhconcert.server.business.domain.reservation;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.business.domain.reservation.exception.ReservationErrorCode;
import com.hhconcert.server.business.domain.reservation.exception.ReservationException;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ReservationTest {

    User user;
    Concert concert;
    Schedule schedule;
    Seat seat;

    @BeforeEach
    void setUp() {
        user = new User(1L, "test1234", 80000);
        concert = new Concert(1L, "콘서트명", LocalDate.now(), LocalDate.now().plusDays(1));
        schedule = new Schedule(1L, concert, LocalDate.now());
        seat = new Seat(1L, schedule, "A1", 75000);
    }

    @DisplayName("임시 예약 내역이 생성된다.")
    @Test
    void createTempReserve() {
        Reservation temp = Reservation.createTemp(user, concert, schedule, seat);

        assertThat(temp.getPrice()).isEqualTo(75000);
        assertThat(temp.getStatus()).isEqualTo(ReservationStatus.TEMP);
        assertThat(temp.getExpiredAt()).isNotNull().isAfter(LocalDateTime.now());
    }

    @DisplayName("결제 금액이 일치하지 않는 경우 true를 반환한다.")
    @Test
    void paymentAMountIsNotMatchPrice_thenTrue() {
        int invalidAmount = 70000;

        Reservation temp = Reservation.createTemp(user, concert, schedule, seat);

        boolean result = temp.isNotMatchAmount(invalidAmount);

        assertThat(result).isTrue();
    }

    @DisplayName("최종 예약 상태로 변경 시 조건에 따라 예외가 발생한다.")
    @TestFactory
    Collection<DynamicTest> updateForCompleteTests() {
        return List.of(
            DynamicTest.dynamicTest("예약 상태가 TEMP가 아닌 경우, 예약건을 결제할 수 없다.", () -> {
                Reservation reservation = new Reservation(user, concert, schedule, seat, 75000, ReservationStatus.CANCEL, null);

                assertThatThrownBy(reservation::updateForComplete)
                        .isInstanceOf(ReservationException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ReservationErrorCode.INVALID_RESERVATION_STATUS)
                        .extracting("errorCode")
                        .extracting("status", "message")
                        .containsExactly(HttpStatus.CONFLICT, "결제할 수 없는 예약 내역입니다.");
            }),
            DynamicTest.dynamicTest("임시 예약 만료 시간이 지난 경우, 예약건을 결제할 수 없다.", () -> {
                Reservation reservation = new Reservation(user, concert, schedule, seat, 75000, ReservationStatus.TEMP, LocalDateTime.now().minusMinutes(1));

                assertThatThrownBy(reservation::updateForComplete)
                        .isInstanceOf(ReservationException.class)
                        .hasFieldOrPropertyWithValue("errorCode", ReservationErrorCode.EXPIRED_RESERVATION)
                        .extracting("errorCode")
                        .extracting("status", "message")
                        .containsExactly(HttpStatus.GONE, "임시 예약 시간이 만료되었습니다.");
            })
        );
    }

}