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
     * 대기 순서 채번
     * @return
     */
    Integer nextPriority();

    /**
     * 우선순위가 가장 높은 WAIT 토큰 조회
     * @return
     */
    Token findNextTokenToActivate();

    /**
     * 토큰 조회
     * @param tokenId
     * @return
     */
    Token findToken(String tokenId);

    /**
     * ACTIVE 토큰 개수 조회
     * @param status
     * @return
     */
    int getTokensFor(TokenStatus status);

    /**
     * 대기 순번 업데이트
     */
    void updateWaitingTokensPriority(Integer currentPriority);

    /**
     * 만료된 ACTIVE 토큰 조회
     * @param now
     * @return
     */
    List<Token> getExpiredTokens(LocalDateTime now);

    /**
     * 만료된 토큰 삭제
     * @param token
     */
    void deleteToken(Token token);
}
