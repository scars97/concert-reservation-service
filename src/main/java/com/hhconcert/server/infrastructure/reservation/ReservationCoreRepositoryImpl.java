package com.hhconcert.server.infrastructure.reservation;

import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Repository
@RequiredArgsConstructor
public class ReservationCoreRepositoryImpl implements ReservationRepository {

    private final ReservationJpaRepository jpaRepository;
    private final ReservationRedisRepository redisRepository;

    @Override
    public Reservation createTempReserve(Reservation reservation) {
        return jpaRepository.save(reservation);
    }

    @Override
    public List<Reservation> findReserveBySeatId(Long seatId) {
        return jpaRepository.findReserveBySeatId(seatId);
    }

    @Override
    public Reservation findReserve(Long reserveId) {
        return jpaRepository.findByIdWithLock(reserveId).orElseThrow(() -> new NoSuchElementException("등록되지 않은 예약입니다."));
    }

    @Override
    public Optional<Reservation> getSeatReserve(Long seatId) {
        return jpaRepository.getSeatReserve(seatId);
    }

    @Override
    public void cancel(Long seatId) {
        jpaRepository.updateStatusBySeatId(seatId);
    }

    @Override
    public void addReservedSeatId(Long seatId, Long currentTime) {
        redisRepository.zSetAdd(seatId, currentTime);
    }

    @Override
    public Set<Long> getReservedSeatIds() {
        return redisRepository.zSetGet();
    }

    @Override
    public Set<Long> getExpireReservedSeatIds(Long currentTime) {
        return redisRepository.zSetGetByScore(currentTime);
    }

    @Override
    public Long dropExpireReservedSeatIds(Long currentTime) {
        return redisRepository.zSetRemoveByScore(currentTime);
    }
}
