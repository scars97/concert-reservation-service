package com.hhconcert.server.business.domain.concert.service;

import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.domain.concert.dto.SeatInfo;
import com.hhconcert.server.business.domain.concert.entity.Seat;
import com.hhconcert.server.business.domain.concert.respository.SeatCacheRepository;
import com.hhconcert.server.business.domain.concert.respository.SeatRepository;
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
    private final SeatCacheRepository seatCacheRepository;
    private final ReservationRepository reservationRepository;

    public List<SeatInfo> getAvailableSeats(Long scheduleId) {
        List<SeatInfo> cachedSeats = seatCacheRepository.getAvailableSeats(scheduleId);
        if (cachedSeats != null) {
            return cachedSeats;
        }

        List<Seat> seats = seatRepository.getSeats(scheduleId);

        Set<Long> reservedSeatIds = reservationRepository.getReservedSeatIds();

        List<SeatInfo> availableSeats = seats.stream()
                .filter(seat -> !reservedSeatIds.contains(seat.getId()))
                .map(SeatInfo::from)
                .toList();

        seatCacheRepository.saveAvailableSeats(scheduleId, availableSeats);

        return availableSeats;
    }

    public SeatInfo findSeat(Long seatId) {
        return SeatInfo.from(seatRepository.findSeat(seatId));
    }

}
