package com.hhconcert.server.infrastructure.reservation;

import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reservation r where r.id = :reserveId")
    Optional<Reservation> findByIdWithLock(@Param("reserveId") Long reserveId);

    List<Reservation> findReserveBySeatId(Long seatId);

}
