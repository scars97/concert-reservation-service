package com.hhconcert.server.interfaces;

import com.hhconcert.server.config.ControllerTestSupport;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PaymentControllerTest extends ControllerTestSupport {

    @DisplayName("예약 내역 결제에 성공한다.")
    @Test
    void payment() throws Exception {
        PaymentRequest request = new PaymentRequest("test1234", 1L, 75000);

        when(paymentFacade.payment(request)).thenReturn(fixture.createPayment());

        mockMvc.perform(post("/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", TEST_TOKEN)
                .content(objectMapper.writeValueAsString(request))
        )
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.paymentId", is(1)),
                        jsonPath("$.reserveId", is(1)),
                        jsonPath("$.userId", is("test1234")),
                        jsonPath("$.price", is(75000)),
                        jsonPath("$.status", is("SUCCESS"))
                );
    }

}