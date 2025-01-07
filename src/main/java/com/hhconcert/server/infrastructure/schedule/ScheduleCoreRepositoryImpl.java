package com.hhconcert.server.infrastructure.schedule;

import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.schedule.persistance.ScheduleRepository;
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
