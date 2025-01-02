package com.hhconcert.server.interfaces.api.schedule;

import com.hhconcert.server.interfaces.api.ControllerTestSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ScheduleControllerTest extends ControllerTestSupport {

    @DisplayName("예약 가능한 날짜의 좌석 목록이 조회된다.")
    @Test
    void getSeats() throws Exception {
        mockMvc.perform(get("/schedules/{scheduleId}/seats", 1L)
                .header("Authorization", "Bearer asdf")
        )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.scheduleId", is(1)),
                        jsonPath("$.seats", hasSize(2)),
                        jsonPath("$.seats[0].seatId", is(1)),
                        jsonPath("$.seats[1].seatId", is(2))
                );
    }

}