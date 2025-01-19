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
import com.hhconcert.server.business.domain.seat.persistance.SeatRepository;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.business.domain.user.persistance.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ConcertRepository concertRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    @Transactional
    public ReservationInfo creatTempReserve(ReservationCommand info) {
        User user = userRepository.findUser(info.userId());
        Concert concert = concertRepository.findConcert(info.concertId());
        Schedule schedule = scheduleRepository.findSchedule(info.scheduleId());
        Seat seat = seatRepository.findSeat(info.seatId());

        Reservation reservation = Reservation.createTemp(user, concert, schedule, seat);

        return ReservationInfo.from(reservationRepository.createTempReserve(reservation));
    }

}
