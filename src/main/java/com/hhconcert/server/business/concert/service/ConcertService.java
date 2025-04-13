package com.hhconcert.server.business.concert.service;

import com.hhconcert.server.business.concert.dto.ConcertInfo;
import com.hhconcert.server.business.concert.respository.ConcertRepository;
import com.hhconcert.server.business.concert.domain.Concert;
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
