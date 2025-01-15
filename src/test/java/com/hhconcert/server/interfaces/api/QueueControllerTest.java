package com.hhconcert.server.interfaces.api;

import com.hhconcert.server.interfaces.api.config.ControllerTestSupport;
import com.hhconcert.server.interfaces.api.queues.dto.TokenRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class QueueControllerTest extends ControllerTestSupport {

    @DisplayName("대기열 토큰 발급 요청 시, 토큰 생성에 성공한다.")
    @Test
    void createQueueToken() throws Exception {
        TokenRequest request = new TokenRequest("test1234");

        when(queueFacade.createToken(request)).thenReturn(testUtil.createWaitToken());

        mockMvc.perform(post("/queues")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.tokenId", notNullValue()),
                        jsonPath("$.userId", is("test1234")),
                        jsonPath("$.priority", is(1)),
                        jsonPath("$.status", is("WAIT"))
                );
    }

    @DisplayName("대기열 상태 조회 시, 현재 대기 순서가 반환된다.")
    @Test
    void getQueueStatus() throws Exception {
        String userId = "test1234";
        when(queueFacade.checkQueueStatus(new TokenRequest(userId))).thenReturn(testUtil.createWaitToken());

        mockMvc.perform(get("/queues/{userId}", userId)
                        .header("Authorization", TEST_TOKEN)
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.tokenId", notNullValue()),
                        jsonPath("$.userId", is("test1234")),
                        jsonPath("$.priority", is(1)),
                        jsonPath("$.status", is("WAIT"))
                );
    }
}