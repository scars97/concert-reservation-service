package com.hhconcert.server.infrastructure.persistence.jpa;

import com.hhconcert.server.business.domain.reservation.outbox.entity.ReservationOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationOutboxJpaRepository extends JpaRepository<ReservationOutbox, Long> {

    @Query("select o from ReservationOutbox o where o.reserveId = :reserveId")
    Optional<ReservationOutbox> findByReserveId(@Param("reserveId") Long reserveId);

    @Query("select o from ReservationOutbox o where o.status = 'INIT' and o.publishedAt <= :thresholdTime")
    List<ReservationOutbox> findInitOutboxOlderThan(@Param("thresholdTime") LocalDateTime thresholdTime);
}
