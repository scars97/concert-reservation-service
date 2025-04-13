package com.hhconcert.server.business.domain.concert.service;

import com.hhconcert.server.business.domain.concert.dto.ConcertInfo;
import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.concert.respository.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConcertService {

    private final ConcertRepository concertRepository;

    public List<ConcertInfo> getConcerts() {
        List<Concert> concerts = concertRepository.getConcerts();
        return concerts.stream()
                .map(ConcertInfo::from)
                .toList();
    }

    public ConcertInfo findConcert(Long concertId) {
        return ConcertInfo.from(concertRepository.findConcert(concertId));
    }

}
