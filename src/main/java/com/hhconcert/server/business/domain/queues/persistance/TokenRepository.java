package com.hhconcert.server.business.domain.queues.persistance;

import com.hhconcert.server.business.domain.queues.entity.TokenVO;

import java.util.Set;

public interface TokenRepository {

    /**
     * 대기 토큰 생성
     * @param token
     */
    void addWaitToken(TokenVO token);

    /**
     * 활성화 토큰 생성
     * @param userId
     */
    void addActiveToken(String userId);

    /**
     * 등록된 토큰 조회
     * @param userId
     * @return
     */
    TokenVO findTokenBy(String userId);

    /**
     * 토큰 대기 순서 조회
     * @param userId
     * @return
     */
    Long getRank(String userId);

    /**
     * 활성화된 토큰 개수 조회
     * @return
     */
    Long getCountForActiveTokens();

    /**
     * 대기 상태 토큰 조회
     * @return
     */
    Long getCountForWaitTokens();

    /**
     * 활성화할 대기 토큰 조회
     * @param activationCount
     * @return
     */
    Set<String> getWaitTokensToActivate(long activationCount);

    /**
     * 활성화 토큰 삭제
     * @param currentTime
     */
    void dropExpiredTokens(long currentTime);

    /**
     * 결제 후 토큰 만료 처리
     * @param userId
     */
    void dropTokenByUserId(String userId);
}
