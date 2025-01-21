package com.hhconcert.server.infrastructure.queues;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TokenJpaRepository extends JpaRepository<Token, String> {

    boolean existsByUserId(String userId);

    List<Token> findByStatusOrderByCreatedAtAsc(TokenStatus status, Pageable pageable);

    Optional<Token> findByUserId(String userId);

    int countByStatus(TokenStatus status);

    List<Token> findByStatus(TokenStatus status);

    void deleteByUserId(String userId);

    @Modifying
    @Query("delete from Token t where t.expiredAt < :now")
    void deleteByExpiredAtBefore(@Param("now") LocalDateTime now);

}
