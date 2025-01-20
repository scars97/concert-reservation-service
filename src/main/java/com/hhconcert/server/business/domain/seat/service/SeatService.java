package com.hhconcert.server.business.domain.seat.service;

import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.domain.seat.dto.SeatInfo;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.seat.persistance.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SeatService {

    private final SeatRepository seatRepository;
    private final ReservationRepository reservationRepository;

    public List<SeatInfo> getAvailableSeats(Long scheduleId) {
        List<Seat> seats = seatRepository.getSeats(scheduleId);

        return seats.stream()
                .filter(s -> reservationRepository.getSeatReserve(s.getId()).isEmpty())
                .map(SeatInfo::from)
                .toList();
    }

    public SeatInfo findSeat(Long seatId) {
        return SeatInfo.from(seatRepository.findSeat(seatId));
    }

}
