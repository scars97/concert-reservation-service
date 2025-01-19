package com.hhconcert.server.business.domain.queues;

import com.hhconcert.server.business.domain.queues.dto.TokenInfo;
import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenGenerator;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.business.domain.queues.exception.TokenErrorCode;
import com.hhconcert.server.business.domain.queues.exception.TokenException;
import com.hhconcert.server.business.domain.queues.service.TokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TokenServiceTest {

    private static final int MAX_ACTIVE_TOKEN_COUNT = 10;

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
        List<Token> tokens = new ArrayList<>();
        for (int i = 0; i < MAX_ACTIVE_TOKEN_COUNT; i++) {
            tokens.add(Token.createForActive("test" + i));
        }

        when(tokenRepository.isDuplicate("test1234")).thenReturn(false);
        when(tokenRepository.getTokensBy(TokenStatus.ACTIVE)).thenReturn(tokens);
        when(tokenRepository.createToken(any(Token.class))).thenReturn(waitToken);

        TokenInfo result = tokenService.createToken("test1234");

        assertThat(result)
                .extracting("tokenId", "userId", "priority", "status")
                .containsExactly(tokenId, userId, 1, TokenStatus.WAIT);
    }

    @DisplayName("ACTIVE 상태의 토큰이 생성된다.")
    @Test
    void createTokenForActive() {
        when(tokenRepository.isDuplicate("test1234")).thenReturn(false);
        when(tokenRepository.getTokensBy(TokenStatus.ACTIVE)).thenReturn(List.of(Token.createForActive("test")));
        when(tokenRepository.createToken(any(Token.class))).thenReturn(activeToken);

        TokenInfo result = tokenService.createToken("test1234");

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
                .hasFieldOrPropertyWithValue("errorCode", TokenErrorCode.DUPLICATED_TOKEN)
                .extracting("errorCode")
                .extracting("status", "message")
                .containsExactly(HttpStatus.CONFLICT, "이미 토큰이 존재합니다.");
    }

    @DisplayName("대기열 상태 요청 시, WAIT 상태인 경우 대기 순서가 연산되어 반환된다.")
    @Test
    void checkQueueStatus() {
        Token targetToken = new Token(TokenGenerator.generateToken("test1234"), "test1234", TokenStatus.WAIT, null, null, now);
        Token token1 = new Token(TokenGenerator.generateToken("test1"), "test1", TokenStatus.WAIT, null, null, now.minusMinutes(1));
        Token token2 = new Token(TokenGenerator.generateToken("test2"), "test2", TokenStatus.WAIT, null, null, now.minusMinutes(2));

        when(tokenRepository.findTokenByUserId("test1234")).thenReturn(targetToken);
        when(tokenRepository.getTokensBy(TokenStatus.WAIT)).thenReturn(List.of(token1, token2));

        TokenInfo result = tokenService.checkQueueStatus("test1234");

        assertThat(result.priority()).isEqualTo(3);
    }

}