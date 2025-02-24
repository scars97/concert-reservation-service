package com.hhconcert.server.infrastructure.reservation.outbox;

import com.hhconcert.server.business.domain.reservation.outbox.entity.ReservationOutbox;
import com.hhconcert.server.business.domain.reservation.outbox.repository.ReservationOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class ReservationOutboxRepositoryImpl implements ReservationOutboxRepository {

    private final ReservationOutboxJpaRepository jpaRepository;

    @Override
    public void save(ReservationOutbox outbox) {
        jpaRepository.save(outbox);
    }

    @Override
    public ReservationOutbox findByReserveId(Long reserveId) {
        return jpaRepository.findByReserveId(reserveId).orElseThrow(() -> new NoSuchElementException("예약 이벤트를 찾지 못했습니다."));
    }

    @Override
    public List<ReservationOutbox> findInitOutboxOlderThan(int minutes) {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(minutes);
        return jpaRepository.findInitOutboxOlderThan(fiveMinutesAgo);
    }
}
