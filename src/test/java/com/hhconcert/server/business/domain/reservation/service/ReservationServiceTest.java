package com.hhconcert.server.business.domain.reservation.service;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.concert.persistance.ConcertRepository;
import com.hhconcert.server.business.domain.reservation.dto.ReservationInfo;
import com.hhconcert.server.business.domain.reservation.dto.ReservationResult;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.schedule.persistance.ScheduleRepository;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.seat.persistance.SeatRepository;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.business.domain.user.persistance.UserRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ConcertRepository concertRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private SeatRepository seatRepository;

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

    @DisplayName("임시 예약 내역이 생성된다.")
    @Test
    void createTempReserve() {
        Reservation reservation = new Reservation(1L, user, concert, schedule, seat,
                seat.getPrice(), ReservationStatus.TEMP, LocalDateTime.now().plusMinutes(5));

        when(userRepository.findUser("test1234")).thenReturn(user);
        when(concertRepository.findConcert(1L)).thenReturn(concert);
        when(scheduleRepository.findSchedule(1L)).thenReturn(schedule);
        when(seatRepository.findSeat(1L)).thenReturn(seat);
        when(reservationRepository.createTempReserve(any(Reservation.class))).thenReturn(reservation);

        ReservationResult result = reservationService.creatTempReserve(new ReservationInfo("test1234", 1L, 1L, 1L));

        assertThat(result.user().userId()).isEqualTo("test1234");
        assertThat(result.concert().id()).isEqualTo(1L);
        assertThat(result.schedule().id()).isEqualTo(1L);
        assertThat(result.seat().seatId()).isEqualTo(1L);
        assertThat(result.price()).isEqualTo(75000);
        assertThat(result.status()).isEqualTo(ReservationStatus.TEMP);
    }

    @DisplayName("해당 좌석에 대한 예약 내역이 없다면 false를 반환한다.")
    @Test
    void isSeatReserved_thenFalse() {
        when(reservationRepository.findReserveBySeatId(1L)).thenReturn(List.of());

        boolean result = reservationService.isSeatReserved(1L);

        assertThat(result).isFalse();
    }

    @DisplayName("해당 좌석에 대한 예약 내역이 존재한다면 여러 조건을 통해 좌석 상태를 검증한다.")
    @TestFactory
    Collection<DynamicTest> isSeatReservedTests() {
        return List.of(
            DynamicTest.dynamicTest("1. 예약 상태가 COMPLETE 인 경우 true를 반환한다.", () -> {
                when(reservationRepository.findReserveBySeatId(1L)).thenReturn(List.of(
                    new Reservation(3L, user, concert, schedule, seat,
                            seat.getPrice(), ReservationStatus.COMPLETE, null),
                    new Reservation(2L, user, concert, schedule, seat,
                            seat.getPrice(), ReservationStatus.CANCEL, null),
                    new Reservation(1L, user, concert, schedule, seat,
                            seat.getPrice(), ReservationStatus.CANCEL, null)
                ));

                boolean result = reservationService.isSeatReserved(1L);

                assertThat(result).isTrue();
            }),
            DynamicTest.dynamicTest("2. 예약 상태가 TEMP 이면서 만료 시간이 지나지 않은 경우 true를 반환한다.", () -> {
                when(reservationRepository.findReserveBySeatId(1L)).thenReturn(List.of(
                    new Reservation(3L, user, concert, schedule, seat,
                            seat.getPrice(), ReservationStatus.TEMP, LocalDateTime.now().plusMinutes(3)),
                    new Reservation(2L, user, concert, schedule, seat,
                            seat.getPrice(), ReservationStatus.CANCEL, null),
                    new Reservation(1L, user, concert, schedule, seat,
                            seat.getPrice(), ReservationStatus.CANCEL, null)
                ));

                boolean result = reservationService.isSeatReserved(1L);

                assertThat(result).isTrue();
            }),
            DynamicTest.dynamicTest("3. 예약 상태가 TEMP 이면서 만료 시간이 지난 경우 false를 반환한다.", () -> {
                when(reservationRepository.findReserveBySeatId(1L)).thenReturn(List.of(
                    new Reservation(3L, user, concert, schedule, seat,
                            seat.getPrice(), ReservationStatus.TEMP, LocalDateTime.now().minusSeconds(3)),
                    new Reservation(2L, user, concert, schedule, seat,
                            seat.getPrice(), ReservationStatus.CANCEL, null),
                    new Reservation(1L, user, concert, schedule, seat,
                            seat.getPrice(), ReservationStatus.CANCEL, null)
                ));

                boolean result = reservationService.isSeatReserved(1L);

                assertThat(result).isFalse();
            }),
            DynamicTest.dynamicTest("4. 예약 상태가 CANCEL인 경우 false를 반환한다.", () -> {
                when(reservationRepository.findReserveBySeatId(1L)).thenReturn(List.of(
                        new Reservation(2L, user, concert, schedule, seat,
                                seat.getPrice(), ReservationStatus.CANCEL, null),
                        new Reservation(1L, user, concert, schedule, seat,
                                seat.getPrice(), ReservationStatus.CANCEL, null)
                ));

                boolean result = reservationService.isSeatReserved(1L);

                assertThat(result).isFalse();
            })
        );
    }

}