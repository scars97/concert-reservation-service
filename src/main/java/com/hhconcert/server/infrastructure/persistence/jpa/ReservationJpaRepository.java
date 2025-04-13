package com.hhconcert.server.infrastructure.persistence.jpa;

import com.hhconcert.server.business.reservation.domain.Reservation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ReservationJpaRepository extends JpaRepository<Reservation, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reservation r where r.id = :reserveId")
    Optional<Reservation> findByIdWithLock(@Param("reserveId") Long reserveId);

    List<Reservation> findReserveBySeatId(Long seatId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select r from Reservation r " +
            "where r.seat.id = :seatId " +
            "and (r.status = 'COMPLETE' " +
            "or (r.status = 'TEMP' and r.expiredAt >= CURRENT_TIMESTAMP))")
    Optional<Reservation> getSeatReserve(@Param("seatId") Long seatId);

    @Modifying
    @Query("update Reservation r set r.status = 'CANCEL' where r.seat.id = :seatId")
    void updateStatusBySeatId(@Param("seatId") Long seatId);

}
