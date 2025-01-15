package com.hhconcert.server.business.domain.seat.service;

import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatAvailability {

    private final ReservationRepository reservationRepository;

    public boolean isAvailable(Long seatId) {
        List<Reservation> reservations = reservationRepository.findReserveBySeatId(seatId);

        if (reservations.isEmpty()) {
            return true;
        }

        return reservations.stream()
            .noneMatch(Reservation::isSeatReserved);
    }
}
