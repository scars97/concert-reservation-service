package com.hhconcert.server.business.domain.queues.service;

import com.hhconcert.server.business.domain.queues.dto.TokenResult;
import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenGenerator;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.global.common.exception.TokenException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private TokenRepository tokenRepository;

    String tokenId;
    String userId;
    LocalDateTime now;
    Token waitToken;
    Token activeToken;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now().withNano(0);
        userId = "test1234";
        tokenId = TokenGenerator.generateToken(userId);

        waitToken = Token.builder()
                .tokenId(tokenId)
                .userId(userId)
                .status(TokenStatus.WAIT)
                .build();
        activeToken = Token.builder()
                .tokenId(tokenId)
                .userId(userId)
                .status(TokenStatus.ACTIVE)
                .activeAt(now)
                .expiredAt(now.plusMinutes(5))
                .build();
    }

    @DisplayName("WAIT 상태의 토큰이 생성된다.")
    @Test
    void createTokenForWAIT() {
        when(tokenRepository.isDuplicate("test1234")).thenReturn(false);
        when(tokenRepository.getTokenCountFor(TokenStatus.ACTIVE)).thenReturn(15);
        when(tokenRepository.createToken(any(Token.class))).thenReturn(waitToken);

        TokenResult result = tokenService.createToken("test1234");

        assertThat(result)
                .extracting("tokenId", "userId", "priority", "status")
                .containsExactly(tokenId, userId, 1, TokenStatus.WAIT);
    }

    @DisplayName("ACTIVE 상태의 토큰이 생성된다.")
    @Test
    void createTokenForActive() {
        when(tokenRepository.isDuplicate("test1234")).thenReturn(false);
        when(tokenRepository.getTokenCountFor(TokenStatus.ACTIVE)).thenReturn(5);
        when(tokenRepository.createToken(any(Token.class))).thenReturn(activeToken);

        TokenResult result = tokenService.createToken("test1234");

        assertThat(result)
                .extracting("tokenId", "userId", "priority", "status", "activeAt", "expireAt")
                .containsExactly(tokenId, userId, 0, TokenStatus.ACTIVE, now, now.plusMinutes(5));
    }

    @DisplayName("해당 userId에 등록된 토큰이 존재하는 경우 예외가 발생한다.")
    @Test
    void createToken_duplicateToken() {
        when(tokenRepository.isDuplicate("test1234")).thenReturn(true);

        assertThatThrownBy(() -> tokenService.createToken("test1234"))
                .isInstanceOf(TokenException.class)
                .hasMessage("이미 토큰이 존재합니다.");
    }

    @DisplayName("대기열 상태 요청 시, WAIT 상태인 경우 대기 순서가 연산되어 반환된다.")
    @Test
    void checkQueueStatus() {
        Token targetToken = Token.createForWait("test1234");
        targetToken.setCreatedAt(now);
        Token token1 = Token.createForWait("test1");
        token1.setCreatedAt(now.minusMinutes(4));
        Token token2 = Token.createForWait("test2");
        token2.setCreatedAt(now.minusMinutes(2));

        when(tokenRepository.findTokenByUserId("test1234")).thenReturn(targetToken);
        when(tokenRepository.getTokensBy(TokenStatus.WAIT)).thenReturn(List.of(token1, token2));

        TokenResult result = tokenService.checkQueueStatus("test1234");

        assertThat(result.priority()).isEqualTo(3);
    }

}