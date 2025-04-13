package com.hhconcert.server.business.domain.seat;

import com.hhconcert.server.business.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.concert.domain.Schedule;
import com.hhconcert.server.business.concert.dto.SeatInfo;
import com.hhconcert.server.business.concert.domain.Seat;
import com.hhconcert.server.business.concert.respository.SeatCacheRepository;
import com.hhconcert.server.business.concert.respository.SeatRepository;
import com.hhconcert.server.business.concert.service.SeatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @InjectMocks
    private SeatService seatService;

    @Mock
    private SeatRepository seatRepository;

    @Mock
    private SeatCacheRepository seatCacheRepository;

    @Mock
    private ReservationRepository reservationRepository;

    Schedule mockSchedule;

    @BeforeEach
    void setUp() {
        mockSchedule = mock(Schedule.class);
    }

    @DisplayName("예약 가능한 좌석 목록이 조회된다.")
    @Test
    void getAvailableSeats() {
        Long scheduleId = 1L;
        List<Seat> seats = List.of(
                new Seat(1L, mockSchedule, "A1", 75000),
                new Seat(2L, mockSchedule, "B1", 60000),
                new Seat(3L, mockSchedule, "C1", 50000)
        );

        when(seatCacheRepository.getAvailableSeats(scheduleId)).thenReturn(null);
        when(seatRepository.getSeats(scheduleId)).thenReturn(seats);
        when(reservationRepository.getReservedSeatIds()).thenReturn(Set.of(1L, 2L));

        List<SeatInfo> results = seatService.getAvailableSeats(scheduleId);

        assertThat(results).hasSize(1)
                .extracting("seatId", "seatNumber", "price")
                .containsExactly(
                        tuple(3L, "C1", 50000)
                );

        verify(seatCacheRepository, times(1)).saveAvailableSeats(scheduleId, results);
    }

    @DisplayName("예약 가능한 좌석 목록이 캐시되어 있는 경우, 캐시된 데이터를 반환한다.")
    @Test
    void whenAvailableSeatsIsCached_thenReturnCachedData() {
        Long scheduleId = 1L;
        List<Seat> seats = List.of(
                new Seat(1L, mockSchedule, "A1", 75000),
                new Seat(2L, mockSchedule, "B1", 60000),
                new Seat(3L, mockSchedule, "C1", 50000)
        );
        List<SeatInfo> seatInfos = List.of(
                new SeatInfo(3L, "C1", 50000)
        );

        when(seatCacheRepository.getAvailableSeats(scheduleId)).thenReturn(seatInfos);

        List<SeatInfo> results = seatService.getAvailableSeats(scheduleId);

        assertThat(results).hasSize(1)
                .extracting("seatId", "seatNumber", "price")
                .containsExactly(
                        tuple(3L, "C1", 50000)
                );

        verify(seatRepository, times(0)).getSeats(scheduleId);
        verify(reservationRepository, times(0)).getReservedSeatIds();
        verify(seatCacheRepository, times(0)).saveAvailableSeats(scheduleId, results);
    }

    @DisplayName("단일 좌석을 조회한다.")
    @Test
    void findSeat() {
        Long seatId = 1L;
        Seat seat = new Seat(1L, mockSchedule, "A1", 75000);

        when(seatRepository.findSeat(seatId)).thenReturn(seat);

        SeatInfo result = seatService.findSeat(seatId);

        assertThat(result).extracting("seatId", "seatNumber", "price")
                .containsExactly(1L, "A1", 75000);
    }

}