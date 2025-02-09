package com.hhconcert.server.business.domain.queues;

import com.hhconcert.server.business.domain.queues.dto.TokenInfo;
import com.hhconcert.server.business.domain.queues.entity.TokenGenerator;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.entity.TokenVO;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.business.domain.queues.service.TokenService;
import com.hhconcert.server.global.common.error.ErrorCode;
import com.hhconcert.server.global.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private TokenRepository tokenRepository;

    String tokenId;
    String userId;
    LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().withNano(0);
        userId = "test1234";
        tokenId = TokenGenerator.generateToken(userId);
    }

    @DisplayName("WAIT 상태의 토큰이 생성된다.")
    @Test
    void createTokenForWAIT() {
        when(tokenRepository.getRank(userId)).thenReturn(null);

        TokenInfo result = tokenService.createToken("test1234");

        assertThat(result)
                .extracting("tokenId", "userId", "priority", "status")
                .containsExactly(tokenId, userId, 0L, TokenStatus.WAIT);

        verify(tokenRepository).addWaitToken(any(TokenVO.class));
    }

    @DisplayName("해당 userId에 등록된 토큰이 존재하는 경우 예외가 발생한다.")
    @Test
    void createToken_duplicateToken() {
        when(tokenRepository.getRank("test1234")).thenReturn(0L);

        assertThatThrownBy(() -> tokenService.createToken("test1234"))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATED_TOKEN)
                .extracting("errorCode")
                .extracting("status", "message")
                .containsExactly(HttpStatus.CONFLICT, "이미 토큰이 존재합니다.");
    }

    @DisplayName("대기열 상태 요청 시, WAIT 상태인 경우 대기 순서가 연산되어 반환된다.")
    @Test
    void checkQueueStatus() {
        TokenVO token = new TokenVO(TokenGenerator.generateToken("target"), "target", TokenStatus.WAIT, now, null, null);

        when(tokenRepository.getRank("target")).thenReturn(0L);

        TokenInfo result = tokenService.checkQueueStatus(TokenInfo.from(token));
        assertThat(result.priority()).isEqualTo(1);
    }

    @DisplayName("사용자 ID로 등록된 토큰을 조회한다.")
    @Test
    void findTokenByUserId() {
        TokenVO token = new TokenVO(tokenId, "test1234", TokenStatus.WAIT, now.minusMinutes(1), null, null);

        when(tokenRepository.findTokenBy("test1234")).thenReturn(token);

        TokenInfo result = tokenService.findTokenBy("test1234");

        assertThat(result).extracting("tokenId", "userId", "status")
                .containsExactly(tokenId, "test1234", TokenStatus.WAIT);
    }

}