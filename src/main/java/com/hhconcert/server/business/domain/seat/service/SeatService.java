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

    public List<SeatInfo> getAvailableSeats(Long scheduleId) {
        List<Seat> availableSeats = seatRepository.getSeats(scheduleId);
        return availableSeats.stream()
                .map(SeatInfo::from)
                .toList();
    }

    public SeatInfo findSeat(Long seatId) {
        return SeatInfo.from(seatRepository.findSeat(seatId));
    }

}
