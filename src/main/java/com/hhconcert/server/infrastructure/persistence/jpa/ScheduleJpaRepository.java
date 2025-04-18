package com.hhconcert.server.infrastructure.persistence.jpa;

import com.hhconcert.server.business.concert.domain.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScheduleJpaRepository extends JpaRepository<Schedule, Long> {

    List<Schedule> findByConcertId(Long concertId);
}
