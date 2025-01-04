package com.hhconcert.server.interfaces.api.reservation;

import com.hhconcert.server.interfaces.api.ControllerTestSupport;
import com.hhconcert.server.interfaces.api.concert.dto.ConcertResponse;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReservationControllerTest extends ControllerTestSupport {

    @DisplayName("좌석 예약 성공 시, 임시 예약 내역이 생성된다.")
    @Test
    void createReservation() throws Exception {
        ReservationRequest request = new ReservationRequest("test1234", 1L, 1L, 1L);

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer asdf")
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.reserveId", is(1)),
                        jsonPath("$.schedule", is(LocalDate.of(2025,1,1).toString())),
                        jsonPath("$.seatNumber", is("A1")),
                        jsonPath("$.concert", notNullValue(ConcertResponse.class)),
                        jsonPath("$.price", is(75000)),
                        jsonPath("$.status", is("TEMP")),
                        jsonPath("$.reservedAt", is(LocalDateTime.of(2024,12,30,0,5).withSecond(0).withNano(1).toString())),
                        jsonPath("$.expiredAt", is(LocalDateTime.of(2024,12,30,0,10).withSecond(0).withNano(1).toString()))
                );

    }

}