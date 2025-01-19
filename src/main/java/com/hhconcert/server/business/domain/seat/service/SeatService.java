package com.hhconcert.server.business.domain.seat.service;

import com.hhconcert.server.business.domain.seat.dto.SeatInfo;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.seat.persistance.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SeatService {

    private final SeatRepository seatRepository;
    private final SeatAvailability seatAvailability;

    public List<SeatInfo> getAvailableSeats(Long scheduleId) {
        List<Seat> seats = seatRepository.getSeats(scheduleId);

        return seats.stream()
                .filter(s -> seatAvailability.isAvailable(s.getId()))
                .map(SeatInfo::from)
                .toList();
    }

    public SeatInfo findSeat(Long seatId) {
        return SeatInfo.from(seatRepository.findSeat(seatId));
    }

}
