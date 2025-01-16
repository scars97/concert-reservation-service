package com.hhconcert.server.infrastructure.reservation;

import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findReserveBySeatId(Long seatId);

}
