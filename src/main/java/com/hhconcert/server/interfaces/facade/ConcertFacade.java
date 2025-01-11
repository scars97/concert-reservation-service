package com.hhconcert.server.interfaces.facade;

import com.hhconcert.server.business.domain.concert.dto.ConcertResult;
import com.hhconcert.server.business.domain.concert.service.ConcertService;
import com.hhconcert.server.business.domain.reservation.service.ReservationService;
import com.hhconcert.server.business.domain.schedule.service.ScheduleService;
import com.hhconcert.server.business.domain.seat.dto.SeatResult;
import com.hhconcert.server.business.domain.seat.service.SeatService;
import com.hhconcert.server.interfaces.api.concert.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConcertFacade {

    private final ConcertService concertService;
    private final ScheduleService scheduleService;
    private final SeatService seatService;
    private final ReservationService reservationService;

    // 콘서트 목록 조회
    public List<ConcertResponse> getConcerts() {
        List<ConcertResult> concerts = concertService.getConcerts();
        return concerts.stream()
                .map(ConcertResponse::from)
                .toList();
    }

    // 콘서트 조회
    public ConcertResponse findConcert(Long concertId) {
        return ConcertResponse.from(concertService.findConcert(concertId));
    }

    // 콘서트 예약 가능 날짜 조회
    public ConcertScheduleResponse getSchedules(Long concertId) {
        List<ScheduleResponse> schedules = scheduleService.getSchedulesByConcert(concertId).stream()
                .map(ScheduleResponse::from)
                .toList();
        return ConcertScheduleResponse.from(concertId, schedules);
    }

    // 콘서트 예약 가능 좌석 조회
    public ScheduleSeatResponse getAvailableSeats(Long scheduleId) {
        List<SeatResult> availableSeats = seatService.getAvailableSeats(scheduleId).stream()
                .filter(seat -> !reservationService.isSeatReserved(seat.seatId()))
                .toList();

        List<SeatResponse> responses = availableSeats.stream()
                .map(SeatResponse::from)
                .toList();
        return ScheduleSeatResponse.from(scheduleId, responses);
    };

}
