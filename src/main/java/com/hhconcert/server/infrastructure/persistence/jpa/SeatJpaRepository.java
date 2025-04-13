package com.hhconcert.server.infrastructure.persistence.jpa;

import com.hhconcert.server.business.concert.domain.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SeatJpaRepository extends JpaRepository<Seat, Long> {

    List<Seat> findByScheduleId(Long scheduleId);

    @Query("select s from Seat s where s.id = :seatId")
    Optional<Seat> findByIdWithLock(@Param("seatId") Long seatId);
}
