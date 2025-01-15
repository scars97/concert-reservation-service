package com.hhconcert.server.interfaces.api;

import com.hhconcert.server.interfaces.api.config.ControllerTestSupport;
import com.hhconcert.server.interfaces.api.point.dto.PointRequest;
import com.hhconcert.server.interfaces.api.point.dto.UserRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PointControllerTest extends ControllerTestSupport {

    @DisplayName("포인트 충전에 성공한다.")
    @Test
    void chargePoint() throws Exception {
        PointRequest request = new PointRequest("test1234", 10000);

        when(userPointFacade.chargePoint(request)).thenReturn(testUtil.createUserPoint(request.amount()));

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

        when(userPointFacade.getPoint(new UserRequest("test1234")))
                .thenReturn(testUtil.createUserPoint(10000));

        mockMvc.perform(get("/points/{userId}", "test1234"))
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.userId", is("test1234")),
                        jsonPath("$.point", is(10000))
                );
    }

}