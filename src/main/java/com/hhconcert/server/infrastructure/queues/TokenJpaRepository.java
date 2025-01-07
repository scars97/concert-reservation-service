package com.hhconcert.server.infrastructure.queues;

import com.hhconcert.server.business.domain.queues.entity.Token;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface TokenJpaRepository extends JpaRepository<Token, String> {

    boolean existsByUserId(String userId);

    @Query("select COALESCE(MAX(t.priority), 0) + 1 from Token t where t.status = 'WAIT'")
    Integer nextPriority();

    @Query("select t from Token t where t.status = 'WAIT' order by t.priority asc limit 1")
    Token findNextTokenToActivate();

    @Query("select count(*) from Token t where t.status = :status")
    int findByStatus(@Param("status") TokenStatus status);

    @Modifying(clearAutomatically = true)
    @Query("update Token t set t.priority = t.priority - 1 where t.status = 'WAIT' and t.priority > :priority")
    void updateWaitingTokensPriority(@Param("priority") Integer currentPriority);

    @Query("select t from Token t where t.status = 'ACTIVE' and t.expiredAt <= :now")
    List<Token> getExpiredTokens(@Param("now") LocalDateTime now);
}
