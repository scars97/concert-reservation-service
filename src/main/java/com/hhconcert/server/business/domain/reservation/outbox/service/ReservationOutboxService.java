package com.hhconcert.server.business.domain.reservation.outbox.service;

import com.hhconcert.server.application.event.reservation.ReserveSuccessEvent;
import com.hhconcert.server.business.domain.reservation.outbox.entity.ReservationOutbox;
import com.hhconcert.server.business.domain.reservation.outbox.repository.ReservationOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationOutboxService {

    private final ReservationOutboxRepository outboxRepository;

    public void init(ReserveSuccessEvent event) {
        ReservationOutbox outbox = ReservationOutbox.init(event.reserveId());

        outbox.toPayload(event);

        outboxRepository.save(outbox);
    }

    @Transactional
    public void updateForPublished(ReserveSuccessEvent event) {
        ReservationOutbox outbox = outboxRepository.findByReserveId(event.reserveId());

        outbox.updateForPublished();
    }

    public List<ReservationOutbox> findInitOutboxOlderThan(int minutes) {
        return outboxRepository.findInitOutboxOlderThan(minutes);
    }
}
