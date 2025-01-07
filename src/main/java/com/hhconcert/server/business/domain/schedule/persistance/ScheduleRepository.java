package com.hhconcert.server.business.domain.schedule.persistance;

import com.hhconcert.server.business.domain.schedule.entity.Schedule;

import java.util.List;

public interface ScheduleRepository {

    List<Schedule> getSchedules(Long concertId);

    Schedule findSchedule(Long scheduleId);
}
