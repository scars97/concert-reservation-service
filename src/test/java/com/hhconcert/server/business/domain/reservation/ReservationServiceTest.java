package com.hhconcert.server.business.domain.reservation;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.concert.respository.ConcertRepository;
import com.hhconcert.server.business.domain.reservation.dto.ReservationCommand;
import com.hhconcert.server.business.domain.reservation.dto.ReservationInfo;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.domain.reservation.service.ReservationService;
import com.hhconcert.server.business.domain.concert.entity.Schedule;
import com.hhconcert.server.business.domain.concert.respository.ScheduleRepository;
import com.hhconcert.server.business.domain.concert.entity.Seat;
import com.hhconcert.server.business.domain.concert.respository.SeatCacheRepository;
import com.hhconcert.server.business.domain.concert.respository.SeatRepository;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.business.domain.user.persistance.UserRepository;
import com.hhconcert.server.global.exception.ErrorCode;
import com.hhconcert.server.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    @Mock
    private SeatCacheRepository seatCacheRepository;

    @Captor
    private ArgumentCaptor<Long> captor;

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
        when(reservationRepository.getSeatReserve(1L)).thenReturn(Optional.empty());

        when(reservationRepository.createTempReserve(any(Reservation.class))).thenReturn(reservation);

        ReservationInfo result = reservationService.creatTempReserve(new ReservationCommand("test1234", 1L, 1L, 1L));

        assertThat(result.user().userId()).isEqualTo("test1234");
        assertThat(result.concert().id()).isEqualTo(1L);
        assertThat(result.schedule().id()).isEqualTo(1L);
        assertThat(result.seat().seatId()).isEqualTo(1L);
        assertThat(result.price()).isEqualTo(75000);
        assertThat(result.status()).isEqualTo(ReservationStatus.TEMP);

        verify(reservationRepository).addReservedSeatId(anyLong(), captor.capture());
        verify(seatCacheRepository, times(1)).evictCacheBy(1L);
    }

    @DisplayName("해당 좌석에 대한 예약건이 존재하는 경우 예외가 발생한다.")
    @Test
    void whenIsSeatReserved_thenThrowException() {
        Reservation seatReserve = new Reservation(1L, user, concert, schedule, seat,
                seat.getPrice(), ReservationStatus.TEMP, LocalDateTime.now().plusMinutes(5));

        when(userRepository.findUser("test1234")).thenReturn(user);
        when(concertRepository.findConcert(1L)).thenReturn(concert);
        when(scheduleRepository.findSchedule(1L)).thenReturn(schedule);
        when(seatRepository.findSeat(1L)).thenReturn(seat);
        when(reservationRepository.getSeatReserve(1L)).thenReturn(Optional.of(seatReserve));

        ReservationCommand command = new ReservationCommand("test1234", 1L, 1L, 1L);

        assertThatThrownBy(() -> reservationService.creatTempReserve(command))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.ALREADY_RESERVED)
                .extracting("errorCode")
                .extracting("status", "message")
                .containsExactly(HttpStatus.CONFLICT, "이미 예약된 좌석입니다.");
    }

}