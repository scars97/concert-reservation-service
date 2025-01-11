package com.hhconcert.server.infrastructure.queues;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenJpaRepository extends JpaRepository<Token, String> {

    boolean existsByUserId(String userId);

    Token findFirstByStatusOrderByCreatedAtAsc(TokenStatus status);

    Optional<Token> findByUserId(String userId);

    int countByStatus(TokenStatus status);

    List<Token> findByStatus(TokenStatus status);

    List<Token> findByStatusAndExpiredAtLessThanEqual(TokenStatus status, LocalDateTime now);

    void deleteByUserId(String userId);

}
