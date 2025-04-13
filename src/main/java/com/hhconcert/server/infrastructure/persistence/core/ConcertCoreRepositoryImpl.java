package com.hhconcert.server.infrastructure.persistence.core;

import com.hhconcert.server.business.concert.domain.Concert;
import com.hhconcert.server.business.concert.respository.ConcertRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.ConcertJpaRepository;
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
