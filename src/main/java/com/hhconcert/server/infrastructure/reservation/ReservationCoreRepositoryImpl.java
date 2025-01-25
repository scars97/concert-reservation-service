package com.hhconcert.server.infrastructure.reservation;

import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ReservationCoreRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository repository;

    @Override
    public Reservation createTempReserve(Reservation reservation) {
        return repository.save(reservation);
    }

    @Override
    public List<Reservation> findReserveBySeatId(Long seatId) {
        return repository.findReserveBySeatId(seatId);
    }

    @Override
    public Reservation findReserve(Long reserveId) {
        return repository.findByIdWithLock(reserveId).orElseThrow(() -> new NoSuchElementException("등록되지 않은 예약입니다."));
    }

    @Override
    public Optional<Reservation> getSeatReserve(Long seatId) {
        return repository.getSeatReserve(seatId);
    }

}
