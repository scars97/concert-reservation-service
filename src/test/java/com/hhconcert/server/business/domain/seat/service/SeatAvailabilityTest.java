package com.hhconcert.server.business.domain.seat.service;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.user.entity.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatAvailabilityTest {

    @InjectMocks
    private SeatAvailability seatAvailability;

    @Mock
    private ReservationRepository reservationRepository;

    User user;
    Concert concert;
    Schedule schedule;
    Seat seat;

    @BeforeEach
    void setUp() {
        user = new User("test1234", 80000);
        concert = new Concert(1L, "콘서트", LocalDate.now(), LocalDate.now().plusDays(1));
        schedule = new Schedule(1L, concert, LocalDate.now());
        seat = new Seat(1L, schedule, "A1", 75000);
    }

    @DisplayName("해당 좌석에 대한 예약 내역이 없다면 True 를 반환한다.")
    @Test
    void isSeatReserved_thenFalse() {
        when(reservationRepository.findReserveBySeatId(1L)).thenReturn(List.of());

        boolean result = seatAvailability.isAvailable(1L);

        assertThat(result).isTrue();
    }

    @DisplayName("해당 좌석에 대한 예약 내역이 존재한다면 여러 조건을 통해 좌석 상태를 검증한다.")
    @TestFactory
    Collection<DynamicTest> isSeatReservedTests() {
        return List.of(
                DynamicTest.dynamicTest("1. 예약 상태가 COMPLETE 인 경우 False 를 반환한다.", () -> {
                    when(reservationRepository.findReserveBySeatId(1L)).thenReturn(List.of(
                            new Reservation(3L, user, concert, schedule, seat,
                                    seat.getPrice(), ReservationStatus.COMPLETE, null),
                            new Reservation(2L, user, concert, schedule, seat,
                                    seat.getPrice(), ReservationStatus.CANCEL, null),
                            new Reservation(1L, user, concert, schedule, seat,
                                    seat.getPrice(), ReservationStatus.CANCEL, null)
                    ));

                    boolean result = seatAvailability.isAvailable(1L);

                    assertThat(result).isFalse();
                }),
                DynamicTest.dynamicTest("2. 예약 상태가 TEMP 이면서 만료 시간이 지나지 않은 경우 False 를 반환한다.", () -> {
                    when(reservationRepository.findReserveBySeatId(1L)).thenReturn(List.of(
                            new Reservation(3L, user, concert, schedule, seat,
                                    seat.getPrice(), ReservationStatus.TEMP, LocalDateTime.now().plusMinutes(3)),
                            new Reservation(2L, user, concert, schedule, seat,
                                    seat.getPrice(), ReservationStatus.CANCEL, null),
                            new Reservation(1L, user, concert, schedule, seat,
                                    seat.getPrice(), ReservationStatus.CANCEL, null)
                    ));

                    boolean result = seatAvailability.isAvailable(1L);

                    assertThat(result).isFalse();
                }),
                DynamicTest.dynamicTest("3. 예약 상태가 TEMP 이면서 만료 시간이 지난 경우 True 를 반환한다.", () -> {
                    when(reservationRepository.findReserveBySeatId(1L)).thenReturn(List.of(
                            new Reservation(3L, user, concert, schedule, seat,
                                    seat.getPrice(), ReservationStatus.TEMP, LocalDateTime.now().minusSeconds(3)),
                            new Reservation(2L, user, concert, schedule, seat,
                                    seat.getPrice(), ReservationStatus.CANCEL, null),
                            new Reservation(1L, user, concert, schedule, seat,
                                    seat.getPrice(), ReservationStatus.CANCEL, null)
                    ));

                    boolean result = seatAvailability.isAvailable(1L);

                    assertThat(result).isTrue();
                }),
                DynamicTest.dynamicTest("4. 예약 상태가 CANCEL인 경우 True 를 반환한다.", () -> {
                    when(reservationRepository.findReserveBySeatId(1L)).thenReturn(List.of(
                            new Reservation(2L, user, concert, schedule, seat,
                                    seat.getPrice(), ReservationStatus.CANCEL, null),
                            new Reservation(1L, user, concert, schedule, seat,
                                    seat.getPrice(), ReservationStatus.CANCEL, null)
                    ));

                    boolean result = seatAvailability.isAvailable(1L);

                    assertThat(result).isTrue();
                })
        );
    }

}