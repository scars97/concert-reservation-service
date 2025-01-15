package com.hhconcert.server.business.persistance;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.schedule.persistance.ScheduleRepository;
import com.hhconcert.server.config.IntegrationTestSupport;
import com.hhconcert.server.infrastructure.concert.ConcertJpaRepository;
import com.hhconcert.server.infrastructure.schedule.ScheduleJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;

class ScheduleRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private ScheduleJpaRepository scheduleJpaRepository;

    @Autowired
    private ConcertJpaRepository concertJpaRepository;

    @DisplayName("특정 콘서트의 예약 날짜 목록이 조회된다.")
    @Test
    void getSchedulesOfConcert() {
        LocalDate now = LocalDate.now();
        Concert concert = concertJpaRepository.save(new Concert("콘서트1", now, now.plusDays(1)));
        scheduleJpaRepository.saveAll(List.of(
                new Schedule(concert, now),
                new Schedule(concert, now.plusDays(1))
        ));

        List<Schedule> results = scheduleRepository.getSchedules(1L);

        assertThat(results).hasSize(2)
                .extracting("concert.id", "id", "date")
                .containsExactly(
                        tuple(1L, 1L, now),
                        tuple(1L, 2L, now.plusDays(1))
                );
    }

    @DisplayName("특정 예약 날짜가 조회된다.")
    @Test
    void findSchedule() {
        LocalDate now = LocalDate.now();
        Concert concert = concertJpaRepository.save(new Concert("콘서트1", now, now.plusDays(1)));
        scheduleJpaRepository.save(new Schedule(concert, now));

        Schedule result = scheduleRepository.findSchedule(1L);

        assertThat(result).extracting("concert.id", "id", "date")
                .containsExactly(1L, 1L, now);
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