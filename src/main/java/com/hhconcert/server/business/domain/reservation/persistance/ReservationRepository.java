package com.hhconcert.server.business.domain.reservation.persistance;

import com.hhconcert.server.business.domain.reservation.entity.Reservation;

import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    /**
     * 예약 취소
     * @param seatId
     */
    void cancel(Long seatId);

    /**
     * 예약된 좌석 번호 등록 
     * @param seatId
     * @param currentTime
     */
    void addReservedSeat(Long seatId, Long currentTime);

    /**
     * 예약된 좌석 번호 목록 조회
     * @return
     */
    Set<Long> getReservedSeats();

    /**
     * 예약이 만료된 좌석 번호 목록 조회
     * @param currentTime
     * @return
     */
    Set<Long> getExpireReservedSeats(Long currentTime);

    /**
     * 예약이 만료된 좌석 번호 삭제
     * @param currentTime
     * @return
     */
    Long dropExpireReservedSeat(Long currentTime);
}
