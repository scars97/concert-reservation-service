package com.hhconcert.server.business.domain.queues.persistance;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface TokenRepository {

    /**
     * 토큰 중복 검증
     * @param userId
     * @return
     */
    boolean isDuplicate(String userId);

    /**
     * 토큰 생성
     * @param token
     * @return
     */
    Token createToken(Token token);

    /**
     * 우선 순위가 높은 WAIT 토큰 목록 조회
     * @param status
     * @param limit
     * @return
     */
    List<Token> findNextTokensToActivate(TokenStatus status, int limit);

    /**
     * 토큰 조회
     * @param tokenId
     * @return
     */
    Token findToken(String tokenId);

    /**
     * 사용자 토큰 조회
     * @param userId
     * @return
     */
    Token findTokenByUserId(String userId);

    /**
     * 특정 상태의 토큰 개수 조회
     * @param status
     * @return
     */
    int getTokenCountFor(TokenStatus status);

    /**
     * 특정 상태의 토큰 목록 조회
     * @param status
     * @return
     */
    List<Token> getTokensBy(TokenStatus status);

    /**
     * 만료된 토큰 삭제
     * @param now
     */
    void dropExpiredTokens(LocalDateTime now);

    /**
     * 결제 후 토큰 만료 처리
     * @param userId
     */
    void dropTokenByUserId(String userId);
}
