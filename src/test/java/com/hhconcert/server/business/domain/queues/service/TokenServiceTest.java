package com.hhconcert.server.business.domain.queues.service;

import com.hhconcert.server.business.domain.queues.dto.TokenResult;
import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.global.exception.TokenException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    @Mock
    private TokenRepository tokenRepository;

    @DisplayName("WAIT 상태의 토큰이 생성된다.")
    @Test
    void createTokenForWAIT() {
        String tokenId = UUID.randomUUID().toString();
        Token queueToken = Token.builder()
                .tokenId(tokenId)
                .userId("test1234")
                .status(TokenStatus.WAIT)
                .priority(1)
                .build();

        when(tokenRepository.isDuplicate("test1234")).thenReturn(false);
        when(tokenRepository.getTokensFor(TokenStatus.ACTIVE)).thenReturn(15);
        when(tokenRepository.createToken(any(Token.class))).thenReturn(queueToken);

        TokenResult result = tokenService.createToken("test1234");

        assertThat(result)
                .extracting("tokenId", "userId", "priority", "status")
                .containsExactly(tokenId, "test1234", 1, TokenStatus.WAIT);
    }

    @DisplayName("ACTIVE 상태의 토큰이 생성된다.")
    @Test
    void createTokenForActive() {
        String tokenId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Token queueToken = Token.builder()
                .tokenId(tokenId)
                .userId("test1234")
                .status(TokenStatus.ACTIVE)
                .activeAt(now)
                .expiredAt(now.plusMinutes(5))
                .build();

        when(tokenRepository.isDuplicate("test1234")).thenReturn(false);
        when(tokenRepository.getTokensFor(TokenStatus.ACTIVE)).thenReturn(5);
        when(tokenRepository.createToken(any(Token.class))).thenReturn(queueToken);

        TokenResult result = tokenService.createToken("test1234");

        assertThat(result)
                .extracting("tokenId", "userId", "status", "activeAt", "expireAt")
                .containsExactly(tokenId, "test1234", TokenStatus.ACTIVE, now, now.plusMinutes(5));
    }

    @DisplayName("해당 userId에 등록된 토큰이 존재하는 경우 예외가 발생한다.")
    @Test
    void createToken_duplicateToken() {
        when(tokenRepository.isDuplicate("test1234")).thenReturn(true);

        assertThatThrownBy(() -> tokenService.createToken("test1234"))
                .isInstanceOf(TokenException.class)
                .hasMessage("이미 토큰이 존재합니다.");
    }

    @DisplayName("사용자 순서 도달 시, 토큰 상태가 ACTIVE로 수정된다.")
    @Test
    void checkQueueStatus_whenReachingOrder_thenUpdateTokenInfo() {
        String tokenId = UUID.randomUUID().toString();
        Token token = Token.builder()
                .tokenId(tokenId)
                .userId("test1234")
                .status(TokenStatus.WAIT)
                .priority(1)
                .build();

        when(tokenRepository.findToken(tokenId)).thenReturn(token);
        when(tokenRepository.getTokensFor(TokenStatus.ACTIVE)).thenReturn(7);

        TokenResult result = tokenService.checkQueueStatus(tokenId);

        assertThat(result.status()).isEqualTo(TokenStatus.ACTIVE);
        assertThat(token.getActiveAt()).isBefore(token.getExpiredAt());
        assertThat(token.getExpiredAt()).isAfter(token.getActiveAt());
    }

    @DisplayName("토큰 만료 처리 시, 토큰 상태가 EXPIRE 로 수정된다.")
    @Test
    void expireToken() {
        String tokenId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Token token = Token.builder()
                .tokenId(tokenId)
                .userId("test1234")
                .status(TokenStatus.ACTIVE)
                .activeAt(now)
                .expiredAt(now.plusMinutes(5))
                .build();

        when(tokenRepository.findToken(tokenId)).thenReturn(token);

        tokenService.expireToken(tokenId);

        verify(tokenRepository, times(1)).dropToken(token);
    }

    @DisplayName("토큰 만료 처리 시, ACTIVE 상태가 아닌 토큰인 경우 예외가 발생한다.")
    @Test
    void expireToken_whenTokenIsNotActive_thenThrowException() {
        String tokenId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now().withNano(0);
        Token token = Token.builder()
                .tokenId(tokenId)
                .userId("test1234")
                .status(TokenStatus.WAIT)
                .priority(3)
                .build();

        when(tokenRepository.findToken(tokenId)).thenReturn(token);

        assertThatThrownBy(() -> tokenService.expireToken(tokenId))
                .isInstanceOf(TokenException.class)
                .hasMessage("유효하지 않은 토큰입니다.");
    }
}