package com.hhconcert.server.infrastructure.seat;

import com.hhconcert.server.business.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByScheduleId(Long scheduleId);
}
