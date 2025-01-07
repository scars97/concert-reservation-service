package com.hhconcert.server.infrastructure.seat;

import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.seat.persistance.SeatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class SeatCoreRepositoryImpl implements SeatRepository {

    private final SeatJpaRepository repository;

    @Override
    public List<Seat> getAvailableSeats(Long scheduleId) {
        return repository.findByScheduleId(scheduleId);
    }

    @Override
    public Seat findSeat(Long seatId) {
        return repository.findById(seatId).orElseThrow(() -> new NoSuchElementException("등록되지 않은 좌석입니다."));
    }

}
