package com.hhconcert.server.business.reservation.service;

import com.hhconcert.server.business.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.concert.respository.SeatCacheRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@EnableScheduling
@Component
@RequiredArgsConstructor
public class ReservationExpireScheduler {

    private final ReservationRepository reservationRepository;
    private final SeatCacheRepository seatCacheRepository;

    @Scheduled(cron = "*/10 * * * * *") // 10초마다 실행
    @Transactional
    public void expireReservedSeats() {
        Long currentTime = System.currentTimeMillis();

        Set<Long> expireReservedSeats = reservationRepository.getExpireReservedSeatIds(currentTime);

        reservationRepository.dropExpireReservedSeatIds(currentTime);

        for (Long seatId : expireReservedSeats) {
            reservationRepository.cancel(seatId);
        }

        seatCacheRepository.evictAllCache();
    }
}
