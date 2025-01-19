package com.hhconcert.server.interfaces;

import com.hhconcert.server.interfaces.api.concert.dto.ConcertResponse;
import com.hhconcert.server.config.ControllerTestSupport;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReservationControllerTest extends ControllerTestSupport {

    @DisplayName("좌석 예약 성공 시, 임시 예약 내역이 생성된다.")
    @Test
    void createReservation() throws Exception {
        ReservationRequest request = new ReservationRequest("test1234", 1L, 1L, 1L);

        LocalDateTime now = LocalDateTime.now().withNano(0);
        when(reservationFacade.tempReserve(request)).thenReturn(fixture.createTempReserve(now));

        mockMvc.perform(post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", TEST_TOKEN)
                        .content(objectMapper.writeValueAsString(request))
        )
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.reserveId", is(1)),
                        jsonPath("$.schedule", is(LocalDate.now().toString())),
                        jsonPath("$.seatNumber", is("A1")),
                        jsonPath("$.concert", notNullValue(ConcertResponse.class)),
                        jsonPath("$.price", is(75000)),
                        jsonPath("$.status", is("TEMP")),
                        jsonPath("$.createdAt", is(now.toString())),
                        jsonPath("$.expiredAt", is(now.plusMinutes(5).toString()))
                );

    }

}