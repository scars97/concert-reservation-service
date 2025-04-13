package com.hhconcert.server.business.reservation.persistance;

import com.hhconcert.server.business.reservation.domain.ReservationOutbox;

import java.util.List;

public interface ReservationOutboxRepository {

    void save(ReservationOutbox outbox);

    ReservationOutbox findByReserveId(Long reserveId);

    List<ReservationOutbox> findInitOutboxOlderThan(int minutes);

}
