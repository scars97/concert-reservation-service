package com.hhconcert.server.business.concert.service;

import com.hhconcert.server.business.concert.dto.ScheduleInfo;
import com.hhconcert.server.business.concert.domain.Schedule;
import com.hhconcert.server.business.concert.respository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public List<ScheduleInfo> getSchedulesByConcert(Long concertId) {
        List<Schedule> schedules = scheduleRepository.getSchedules(concertId);
        return schedules.stream()
                .map(ScheduleInfo::from)
                .toList();
    }

    public ScheduleInfo findSchedule(Long scheduleId) {
        return ScheduleInfo.from(scheduleRepository.findSchedule(scheduleId));
    }
}
