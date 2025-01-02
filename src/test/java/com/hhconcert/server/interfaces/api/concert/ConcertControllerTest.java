package com.hhconcert.server.interfaces.api.concert;

import com.hhconcert.server.interfaces.api.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ConcertControllerTest extends ControllerTestSupport {

    @DisplayName("콘서트 목록을 조회한다.")
    @Test
    void getConcerts() throws Exception {

        mockMvc.perform(get("/concerts"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(2)),
                        jsonPath("$.[0].id", is(1)),
                        jsonPath("$.[0].title", is("콘서트명1")),
                        jsonPath("$.[0].startDate", is(LocalDate.of(2024,12,31).toString())),
                        jsonPath("$.[0].endDate", is(LocalDate.of(2025,1,1).toString())),
                        jsonPath("$.[1].id", is(2)),
                        jsonPath("$.[1].title", is("콘서트명2")),
                        jsonPath("$.[1].startDate", is(LocalDate.of(2025,1,1).toString())),
                        jsonPath("$.[1].endDate", is(LocalDate.of(2025,1,2).toString()))
                );
    }

    @DisplayName("특정 콘서트를 조회한다.")
    @Test
    void findConcert() throws Exception {
        Long concertId = 1L;

        mockMvc.perform(get("/concerts/{concertId}",concertId))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(1)),
                        jsonPath("$.title", is("콘서트명1")),
                        jsonPath("$.startDate", is(LocalDate.of(2024,12,31).toString())),
                        jsonPath("$.endDate", is(LocalDate.of(2025,1,1).toString()))
                );
    }

    @DisplayName("등록되지 않은 콘서트 조회 시, 예외가 발생한다.")
    @Test
    void invalidConcert_thenThrowException() throws Exception {
        Long invalidId = 99L;

        mockMvc.perform(get("/concerts/{concertId}", invalidId))
                .andExpectAll(
                        status().isNotFound(),
                        jsonPath("$.status", is(HttpStatus.NOT_FOUND.toString())),
                        jsonPath("$.message", is("등록되지 않은 콘서트입니다."))
                );
    }

    @DisplayName("특정 콘서트에 대한 예약 가능한 날짜를 조회한다.")
    @Test
    void findConcertSchedule() throws Exception {

        mockMvc.perform(get("/concerts/{concertId}/schedules", 1L)
                        .header("Authorization", "Bearer asdf")
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.concertId", is(1)),
                        jsonPath("$.schedules", hasSize(2)),
                        jsonPath("$.schedules[0].scheduleId", is(1)),
                        jsonPath("$.schedules[0].date", is(LocalDate.of(2024,12,31).toString())),
                        jsonPath("$.schedules[1].scheduleId", is(2)),
                        jsonPath("$.schedules[1].date", is(LocalDate.of(2025,1,1).toString()))
                );
    }

}