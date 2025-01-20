package com.hhconcert.server.business.domain.reservation.persistance;

import com.hhconcert.server.business.domain.reservation.entity.Reservation;

import java.util.List;
import java.util.Optional;

public interface ReservationRepository {

    /**
     * 임시 예약 내역 생성
     * @param reservation
     * @return
     */
    Reservation createTempReserve(Reservation reservation);

    /**
     * 해당 좌석에 대한 예약 내역 조회
     * @param seatId
     * @return
     */
    List<Reservation> findReserveBySeatId(Long seatId);

    /**
     * 예약 내역 조회
     * @param reserveId
     * @return
     */
    Reservation findReserve(Long reserveId);

    /**
     * 해당 좌석 예약 목록 조회
     * @param seatId
     * @return
     */
    Optional<Reservation> getSeatReserve(Long seatId);
}
