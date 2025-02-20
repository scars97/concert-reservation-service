package com.hhconcert.server.business.domain.reservation.outbox.repository;

import com.hhconcert.server.business.domain.reservation.outbox.entity.ReservationOutbox;

import java.util.List;

public interface ReservationOutboxRepository {

    void save(ReservationOutbox outbox);

    ReservationOutbox findByReserveId(Long reserveId);

    List<ReservationOutbox> findInitOutboxOlderThan(int minutes);

}
