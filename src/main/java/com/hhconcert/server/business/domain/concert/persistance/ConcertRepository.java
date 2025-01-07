package com.hhconcert.server.business.domain.concert.persistance;

import com.hhconcert.server.business.domain.concert.entity.Concert;

import java.util.List;

public interface ConcertRepository {

    /**
     * 콘서트 목록 조회
     * @return
     */
    List<Concert> getConcerts();

    /**
     * 콘서트 조회
     * @param concertId
     * @return
     */
    Concert findConcert(Long concertId);
}
