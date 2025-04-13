package com.hhconcert.server.business.domain.concert.respository;

import com.hhconcert.server.business.domain.concert.entity.Schedule;

import java.util.List;

public interface ScheduleRepository {

    List<Schedule> getSchedules(Long concertId);

    Schedule findSchedule(Long scheduleId);
}
