package com.hhconcert.server.interfaces.api.payment;

import com.hhconcert.server.interfaces.api.ControllerTestSupport;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MockPaymentControllerTest extends ControllerTestSupport {

    @DisplayName("예약 내역 결제에 성공한다.")
    @Test
    void payment() throws Exception {
        PaymentRequest request = new PaymentRequest("test1234", 1L, 75000);

        mockMvc.perform(post("/mock/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer asdf")
                .content(objectMapper.writeValueAsString(request))
        )
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.paymentId", is(1)),
                        jsonPath("$.reserveId", is(1)),
                        jsonPath("$.userId", is("test1234")),
                        jsonPath("$.price", is(75000)),
                        jsonPath("$.status", is("SUCCESS")),
                        jsonPath("$.createdAt", is(LocalDateTime.of(2024,12,30,0,13,0,1).toString()))
                );
    }

}