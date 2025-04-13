package com.hhconcert.server.business.reservation.service;

import com.hhconcert.server.business.concert.domain.Schedule;
import com.hhconcert.server.business.concert.domain.Seat;
import com.hhconcert.server.business.concert.respository.SeatRepository;
import com.hhconcert.server.business.concert.domain.Concert;
import com.hhconcert.server.business.concert.respository.ConcertRepository;
import com.hhconcert.server.business.reservation.dto.ReservationCommand;
import com.hhconcert.server.business.reservation.dto.ReservationInfo;
import com.hhconcert.server.business.reservation.domain.Reservation;
import com.hhconcert.server.business.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.concert.respository.ScheduleRepository;
import com.hhconcert.server.business.concert.respository.SeatCacheRepository;
import com.hhconcert.server.business.user.domain.User;
import com.hhconcert.server.business.user.persistance.UserRepository;
import com.hhconcert.server.global.exception.ErrorCode;
import com.hhconcert.server.global.exception.BusinessException;
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

        // 좌석 조회 시 최신화된 예약 좌석 ID를 읽을 수 있도록 하기 위함.
        reservationRepository.addReservedSeatId(seat.getId(), System.currentTimeMillis());

        seatCacheRepository.evictCacheBy(schedule.getId());

        return ReservationInfo.from(reservationRepository.createTempReserve(reservation));
    }

}
