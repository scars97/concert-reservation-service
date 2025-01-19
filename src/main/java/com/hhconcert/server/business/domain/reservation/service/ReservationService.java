package com.hhconcert.server.business.domain.reservation.service;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.concert.persistance.ConcertRepository;
import com.hhconcert.server.business.domain.reservation.dto.ReservationCommand;
import com.hhconcert.server.business.domain.reservation.dto.ReservationInfo;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.exception.ReservationErrorCode;
import com.hhconcert.server.business.domain.reservation.exception.ReservationException;
import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.schedule.persistance.ScheduleRepository;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.seat.persistance.SeatRepository;
import com.hhconcert.server.business.domain.seat.service.SeatAvailability;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.business.domain.user.persistance.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ConcertRepository concertRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final SeatAvailability seatAvailability;

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ReservationInfo creatTempReserve(ReservationCommand info) {
        User user = userRepository.findUser(info.userId());
        Concert concert = concertRepository.findConcert(info.concertId());
        Schedule schedule = scheduleRepository.findSchedule(info.scheduleId());
        Seat seat = seatRepository.findSeat(info.seatId());

        if (!seatAvailability.isAvailable(seat.getId())) {
            throw new ReservationException(ReservationErrorCode.ALREADY_RESERVED);
        }

        Reservation reservation = Reservation.createTemp(user, concert, schedule, seat);

        return ReservationInfo.from(reservationRepository.createTempReserve(reservation));
    }

}
