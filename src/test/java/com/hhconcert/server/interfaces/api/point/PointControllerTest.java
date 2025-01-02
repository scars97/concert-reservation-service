package com.hhconcert.server.interfaces.api.point;

import com.hhconcert.server.interfaces.api.ControllerTestSupport;
import com.hhconcert.server.interfaces.api.point.dto.PointRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class PointControllerTest extends ControllerTestSupport {

    @DisplayName("포인트 충전에 성공한다.")
    @Test
    void chargePoint() throws Exception {
        PointRequest request = new PointRequest("test1234", 10000);

        mockMvc.perform(patch("/points")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.userId", is("test1234")),
                        jsonPath("$.point", is(10000))
                );
    }

    @DisplayName("포인트 조회에 성공한다.")
    @Test
    void getPoint() throws Exception {
        mockMvc.perform(get("/points/{userId}", "test1234"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.userId", is("test1234")),
                        jsonPath("$.point", is(300000))
                );
    }

}