package com.hhconcert.server.business.domain.reservation.service;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.concert.persistance.ConcertRepository;
import com.hhconcert.server.business.domain.reservation.dto.ReservationCommand;
import com.hhconcert.server.business.domain.reservation.dto.ReservationInfo;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.schedule.persistance.ScheduleRepository;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.seat.persistance.SeatCacheRepository;
import com.hhconcert.server.business.domain.seat.persistance.SeatRepository;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.business.domain.user.persistance.UserRepository;
import com.hhconcert.server.global.common.error.ErrorCode;
import com.hhconcert.server.global.common.exception.BusinessException;
import com.hhconcert.server.global.common.lock.DistributedLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ConcertRepository concertRepository;
    private final ScheduleRepository scheduleRepository;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;
    private final SeatCacheRepository seatCacheRepository;

    @DistributedLock(key = "#info.seatId()")
    @Transactional
    public ReservationInfo creatTempReserve(ReservationCommand info) {
        User user = userRepository.findUser(info.userId());
        Concert concert = concertRepository.findConcert(info.concertId());
        Schedule schedule = scheduleRepository.findSchedule(info.scheduleId());
        Seat seat = seatRepository.findSeat(info.seatId());

        reservationRepository.getSeatReserve(seat.getId())
                .ifPresent(reservation -> {
                    throw new BusinessException(ErrorCode.ALREADY_RESERVED);
                });

        Reservation reservation = Reservation.createTemp(user, concert, schedule, seat);

        reservationRepository.addReservedSeatId(seat.getId(), System.currentTimeMillis());

        seatCacheRepository.evictCacheBy(schedule.getId());

        return ReservationInfo.from(reservationRepository.createTempReserve(reservation));
    }

}
