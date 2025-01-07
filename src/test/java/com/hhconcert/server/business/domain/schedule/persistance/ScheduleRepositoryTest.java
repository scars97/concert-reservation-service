package com.hhconcert.server.business.domain.schedule.persistance;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.infrastructure.concert.ConcertJpaRepository;
import com.hhconcert.server.infrastructure.schedule.ScheduleJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest
class ScheduleRepositoryTest {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleJpaRepository scheduleJpaRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    LocalDate now;
    Concert concert;
    List<Schedule> schedules;

    @BeforeEach
    void setUp() {
        now = LocalDate.now();
        concert = concertJpaRepository.save(new Concert("콘서트1", now, now.plusDays(1)));

        schedules = scheduleJpaRepository.saveAll(List.of(
                new Schedule(concert, now),
                new Schedule(concert, now.plusDays(1))
        ));
    }

    @DisplayName("특정 콘서트의 예약 날짜 목록이 조회된다.")
    @Test
    void getSchedulesOfConcert() {
        Long concertId = concert.getId();

        List<Schedule> results = scheduleRepository.getSchedules(concertId);

        assertThat(results).hasSize(2)
                .extracting("concert.id", "date")
                .containsExactly(
                        tuple(concertId, now),
                        tuple(concertId, now.plusDays(1))
                );
    }

    @DisplayName("특정 예약 날짜가 조회된다.")
    @Test
    void findSchedule() {
        Long concertId = concert.getId();
        Long scheduleId = schedules.get(0).getId();

        Schedule result = scheduleRepository.findSchedule(scheduleId);

        assertThat(result).extracting("concert.id", "date")
                .containsExactly(concertId, now);
    }

    @DisplayName("등록되지 않은 일정 조회 시, 예외가 발생한다.")
    @Test
    void whenInvalidScheduleId_thenThrowException() {
        Long invalidScheduleId = 99L;

        assertThatThrownBy(() -> scheduleRepository.findSchedule(invalidScheduleId))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("등록되지 않은 일정입니다.");
    }
}