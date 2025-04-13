package com.hhconcert.server.application.facade;

import com.hhconcert.server.application.dto.ConcertResult;
import com.hhconcert.server.application.dto.ScheduleResult;
import com.hhconcert.server.application.dto.SeatResult;
import com.hhconcert.server.business.domain.concert.dto.ConcertInfo;
import com.hhconcert.server.business.domain.concert.service.ConcertService;
import com.hhconcert.server.business.domain.concert.dto.ScheduleInfo;
import com.hhconcert.server.business.domain.concert.service.ScheduleService;
import com.hhconcert.server.business.domain.concert.dto.SeatInfo;
import com.hhconcert.server.business.domain.concert.service.SeatService;
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

    public List<ConcertResult> getConcerts() {
        List<ConcertInfo> concerts = concertService.getConcerts();
        return concerts.stream()
                .map(ConcertResult::from)
                .toList();
    }

    public ConcertResult findConcert(ConcertRequest request) {
        return ConcertResult.from(concertService.findConcert(request.concertId()));
    }

    public List<ScheduleResult> getSchedules(ConcertRequest request) {
        List<ScheduleInfo> schedules = scheduleService.getSchedulesByConcert(request.concertId());
        return schedules.stream()
                .map(ScheduleResult::from)
                .toList();
    }

    public List<SeatResult> getAvailableSeats(ScheduleRequest request) {
        List<SeatInfo> availableSeats = seatService.getAvailableSeats(request.scheduleId());
        return availableSeats.stream()
                .map(SeatResult::from)
                .toList();
    }

}
