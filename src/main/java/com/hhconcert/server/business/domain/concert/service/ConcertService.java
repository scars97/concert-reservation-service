package com.hhconcert.server.business.domain.concert.service;

import com.hhconcert.server.business.domain.concert.dto.ConcertResult;
import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.concert.persistance.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;

    public List<ConcertResult> getConcerts() {
        List<Concert> concerts = concertRepository.getConcerts();
        return concerts.stream()
                .map(ConcertResult::from)
                .toList();
    }

    public ConcertResult findConcert(Long concertId) {
        return ConcertResult.from(concertRepository.findConcert(concertId));
    }

}
