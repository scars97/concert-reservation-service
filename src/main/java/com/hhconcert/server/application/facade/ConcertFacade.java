package com.hhconcert.server.application.facade;

import com.hhconcert.server.application.dto.ConcertResult;
import com.hhconcert.server.application.dto.ScheduleResult;
import com.hhconcert.server.application.dto.SeatResult;
import com.hhconcert.server.business.domain.concert.dto.ConcertInfo;
import com.hhconcert.server.business.domain.concert.service.ConcertService;
import com.hhconcert.server.business.domain.reservation.service.ReservationService;
import com.hhconcert.server.business.domain.schedule.service.ScheduleService;
import com.hhconcert.server.business.domain.seat.dto.SeatInfo;
import com.hhconcert.server.business.domain.seat.service.SeatService;
import com.hhconcert.server.interfaces.api.concert.dto.ConcertRequest;
import com.hhconcert.server.interfaces.api.concert.dto.ScheduleRequest;
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
    public List<ConcertResult> getConcerts() {
        List<ConcertInfo> concerts = concertService.getConcerts();
        return concerts.stream()
                .map(ConcertResult::from)
                .toList();
    }

    // 콘서트 조회
    public ConcertResult findConcert(ConcertRequest request) {
        return ConcertResult.from(concertService.findConcert(request.concertId()));
    }

    // 콘서트 예약 가능 날짜 조회
    public List<ScheduleResult> getSchedules(ConcertRequest request) {
        return scheduleService.getSchedulesByConcert(request.concertId())
                .stream()
                .map(ScheduleResult::from)
                .toList();
    }

    // 콘서트 예약 가능 좌석 조회
    public List<SeatResult> getAvailableSeats(ScheduleRequest request) {
        List<SeatInfo> availableSeats = seatService.getAvailableSeats(request.scheduleId()).stream()
                .filter(seat -> !reservationService.isSeatReserved(seat.seatId()))
                .toList();

        return availableSeats.stream()
                .map(SeatResult::from)
                .toList();
    };

}
