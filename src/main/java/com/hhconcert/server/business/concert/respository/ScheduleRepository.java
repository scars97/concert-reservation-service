package com.hhconcert.server.business.concert.respository;

import com.hhconcert.server.business.concert.domain.Schedule;

import java.util.List;

public interface ScheduleRepository {

    List<Schedule> getSchedules(Long concertId);

    Schedule findSchedule(Long scheduleId);
}
