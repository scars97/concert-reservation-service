package com.hhconcert.server.infrastructure.persistence.jpa;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConcertJpaRepository extends JpaRepository<Concert, Long> {
}
