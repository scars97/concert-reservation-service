package com.hhconcert.server.infrastructure.concert;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.concert.persistance.ConcertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class ConcertCoreRepositoryImpl implements ConcertRepository {

    private final ConcertJpaRepository repository;

    @Override
    public List<Concert> getConcerts() {
        return repository.findAll();
    }

    @Override
    public Concert findConcert(Long concertId) {
        return repository.findById(concertId).orElseThrow(() -> new NoSuchElementException("등록되지 않은 콘서트입니다."));
    }
}
