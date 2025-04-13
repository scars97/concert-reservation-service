package com.hhconcert.server.infrastructure.persistence.core;

import com.hhconcert.server.business.domain.concert.entity.Seat;
import com.hhconcert.server.business.domain.concert.respository.SeatRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.SeatJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class SeatCoreRepositoryImpl implements SeatRepository {

    private final SeatJpaRepository repository;

    @Override
    public List<Seat> getSeats(Long scheduleId) {
        return repository.findByScheduleId(scheduleId);
    }

    @Override
    public Seat findSeat(Long seatId) {
        return repository.findByIdWithLock(seatId).orElseThrow(() -> new NoSuchElementException("등록되지 않은 좌석입니다."));
    }

}
