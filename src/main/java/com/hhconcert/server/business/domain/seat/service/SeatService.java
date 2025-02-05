package com.hhconcert.server.business.domain.seat.service;

import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.domain.seat.dto.SeatInfo;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.seat.persistance.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatService {

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public List<SeatInfo> getAvailableSeats(Long scheduleId) {
        List<Seat> seats = seatRepository.getSeats(scheduleId);

        Set<Long> reservedSeatIds = reservationRepository.getReservedSeatIds();

        return seats.stream()
                .filter(seat -> !reservedSeatIds.contains(seat.getId()))
                .map(SeatInfo::from)
                .toList();
    }

    public SeatInfo findSeat(Long seatId) {
        return SeatInfo.from(seatRepository.findSeat(seatId));
    }

}
