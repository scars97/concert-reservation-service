package com.hhconcert.server.business.domain.seat.service;

import com.hhconcert.server.business.domain.seat.dto.SeatResult;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.seat.persistance.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;

    public List<SeatResult> getAvailableSeats(Long scheduleId) {
        List<Seat> availableSeats = seatRepository.getAvailableSeats(scheduleId);
        return availableSeats.stream()
                .map(SeatResult::from)
                .toList();
    }

    public SeatResult findSeat(Long seatId) {
        return SeatResult.from(seatRepository.findSeat(seatId));
    }

}
