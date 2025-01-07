package com.hhconcert.server.interfaces.api.queues;

import com.hhconcert.server.interfaces.api.ControllerTestSupport;
import com.hhconcert.server.interfaces.api.queues.dto.TokenRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class QueueControllerTest extends ControllerTestSupport {

    @DisplayName("대기열 토큰 발급 요청 시, 토큰 생성에 성공한다.")
    @Test
    void createQueueToken() throws Exception {
        TokenRequest request = new TokenRequest("test1234");

        mockMvc.perform(post("/queues/token")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.tokenId", notNullValue()),
                        jsonPath("$.status", is("WAIT"))
                );
    }

    @DisplayName("대기열 상태 조회 시, 현재 대기 순서가 반환된다.")
    @Test
    void getQueueStatus() throws Exception {
        String userId = "test1234";

        mockMvc.perform(get("/queues/{userId}", userId)
                        .header("Authorization", "Bearer asdf")
                )
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.tokenId", notNullValue()),
                        jsonPath("$.status", is("WAIT"))
                );
    }

    @DisplayName("인증 정보가 없는 경우, 예외가 발생한다.")
    @Test
    void unauthorized_thenThrowException() throws Exception {
        mockMvc.perform(get("/queues/{userId}", "test1234")
                )
                .andExpectAll(
                        status().isUnauthorized(),
                        jsonPath("$.status", is(HttpStatus.UNAUTHORIZED.toString())),
                        jsonPath("$.message", is("토큰 정보가 누락되었습니다."))
                );
    }

    @DisplayName("잘못된 토큰인 경우, 예외가 발생한다.")
    @Test
    void invalidToken_thenThrowException() throws Exception {
        mockMvc.perform(get("/queues/{userId}", "test1234")
                        .header("Authorization", "")
                )
                .andExpectAll(
                        status().isForbidden(),
                        jsonPath("$.status", is(HttpStatus.FORBIDDEN.toString())),
                        jsonPath("$.message", is("유효하지 않은 토큰입니다."))
                );
    }
}