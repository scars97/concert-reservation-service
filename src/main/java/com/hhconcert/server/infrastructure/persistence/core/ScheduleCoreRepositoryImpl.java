package com.hhconcert.server.infrastructure.persistence.core;

import com.hhconcert.server.business.concert.domain.Schedule;
import com.hhconcert.server.business.concert.respository.ScheduleRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.ScheduleJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class ScheduleCoreRepositoryImpl implements ScheduleRepository {

    private final ScheduleJpaRepository repository;

    @Override
    public List<Schedule> getSchedules(Long concertId) {
        return repository.findByConcertId(concertId);
    }

    @Override
    public Schedule findSchedule(Long scheduleId) {
        return repository.findById(scheduleId).orElseThrow(() -> new NoSuchElementException("등록되지 않은 일정입니다."));
    }

}
