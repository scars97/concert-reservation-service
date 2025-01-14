package com.hhconcert.server.business.domain.seat.service;

import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.seat.dto.SeatInfo;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.seat.persistance.SeatRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SeatServiceTest {

    @InjectMocks
    private SeatService seatService;

    @Mock
    private SeatRepository seatRepository;

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

        when(seatRepository.getSeats(scheduleId)).thenReturn(seats);

        List<SeatInfo> results = seatService.getAvailableSeats(scheduleId);

        assertThat(results).hasSize(3)
                .extracting("seatId", "seatNumber", "price")
                .containsExactly(
                        tuple(1L, "A1", 75000),
                        tuple(2L, "B1", 60000),
                        tuple(3L, "C1", 50000)
                );
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