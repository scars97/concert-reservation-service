package com.hhconcert.server.interfaces.api;

import com.hhconcert.server.interfaces.api.config.ControllerTestSupport;
import com.hhconcert.server.interfaces.api.concert.dto.ConcertRequest;
import com.hhconcert.server.interfaces.api.concert.dto.ScheduleRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ConcertControllerTest extends ControllerTestSupport {

    @DisplayName("콘서트 목록을 조회한다.")
    @Test
    void getConcerts() throws Exception {

        when(concertFacade.getConcerts()).thenReturn(List.of(testUtil.createConcert()));

        mockMvc.perform(get("/concerts"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$", hasSize(1)),
                        jsonPath("$.[0].id", is(1)),
                        jsonPath("$.[0].title", is("콘서트 1")),
                        jsonPath("$.[0].startDate", is(LocalDate.now().toString())),
                        jsonPath("$.[0].endDate", is(LocalDate.now().plusDays(1).toString()))
                );
    }

    @DisplayName("특정 콘서트를 조회한다.")
    @Test
    void findConcert() throws Exception {
        ConcertRequest request = new ConcertRequest(1L);

        when(concertFacade.findConcert(request)).thenReturn(testUtil.createConcert());

        mockMvc.perform(get("/concerts/{concertId}", 1L))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id", is(1)),
                        jsonPath("$.title", is("콘서트 1")),
                        jsonPath("$.startDate", is(LocalDate.now().toString())),
                        jsonPath("$.endDate", is(LocalDate.now().plusDays(1).toString()))
                );
    }

    @DisplayName("특정 콘서트에 대한 예약 가능한 날짜를 조회한다.")
    @Test
    void findConcertSchedule() throws Exception {
        when(concertFacade.getSchedules(new ConcertRequest(1L))).thenReturn(List.of(testUtil.createSchedule()));

        mockMvc.perform(get("/concerts/{concertId}/schedules", 1L)
                        .header("Authorization", TEST_TOKEN)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.concertId", is(1)),
                        jsonPath("$.schedules", hasSize(1)),
                        jsonPath("$.schedules[0].scheduleId", is(1)),
                        jsonPath("$.schedules[0].date", is(LocalDate.now().toString()))
                );
    }

    @DisplayName("예약 가능한 날짜의 좌석 목록이 조회된다.")
    @Test
    void getAvailableSeats() throws Exception {
        when(concertFacade.getAvailableSeats(new ScheduleRequest(1L)))
                .thenReturn(List.of(testUtil.createSeat()));

        mockMvc.perform(get("/concerts/schedules/{scheduleId}/seats", 1L, 1L)
                        .header("Authorization", TEST_TOKEN)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.scheduleId", is(1)),
                        jsonPath("$.seats", hasSize(1)),
                        jsonPath("$.seats[0].seatId", is(1)),
                        jsonPath("$.seats[0].seatNumber", is("A1")),
                        jsonPath("$.seats[0].price", is(75000))
                );
    }

}