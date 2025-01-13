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
    public ConcertResponse findConcert(ConcertRequest request) {
        return ConcertResponse.from(concertService.findConcert(request.concertId()));
    }

    // 콘서트 예약 가능 날짜 조회
    public ConcertScheduleResponse getSchedules(ConcertRequest request) {
        List<ScheduleResponse> schedules = scheduleService.getSchedulesByConcert(request.concertId()).stream()
                .map(ScheduleResponse::from)
                .toList();
        return ConcertScheduleResponse.from(request.concertId(), schedules);
    }

    // 콘서트 예약 가능 좌석 조회
    public ScheduleSeatResponse getAvailableSeats(ScheduleRequest request) {
        List<SeatResult> availableSeats = seatService.getAvailableSeats(request.scheduleId()).stream()
                .filter(seat -> !reservationService.isSeatReserved(seat.seatId()))
                .toList();

        List<SeatResponse> responses = availableSeats.stream()
                .map(SeatResponse::from)
                .toList();
        return ScheduleSeatResponse.from(request.scheduleId(), responses);
    };

}
