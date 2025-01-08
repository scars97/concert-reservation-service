package com.hhconcert.server.business.domain.reservation.service;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.concert.persistance.ConcertRepository;
import com.hhconcert.server.business.domain.reservation.dto.ReservationInfo;
import com.hhconcert.server.business.domain.reservation.dto.ReservationResult;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.business.domain.reservation.persistance.ReservationRepository;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.schedule.persistance.ScheduleRepository;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.seat.persistance.SeatRepository;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.business.domain.user.persistance.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ConcertRepository concertRepository;
    private final ScheduleRepository scheduleRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;

    public ReservationResult creatTempReserve(ReservationInfo info) {
        User user = userRepository.findUser(info.userId());
        Concert concert = concertRepository.findConcert(info.concertId());
        Schedule schedule = scheduleRepository.findSchedule(info.scheduleId());
        Seat seat = seatRepository.findSeat(info.seatId());

        Reservation reservation = Reservation.createTemp(user, concert, schedule, seat);

        return ReservationResult.from(reservationRepository.createTempReserve(reservation));
    }

    public boolean isSeatReserved(Long seatId) {
        List<Reservation> reservations = reservationRepository.findReserveBySeatId(seatId);

        if (reservations.isEmpty()) {
            return false;
        }

        return reservations.stream()
                .anyMatch(r ->
                    r.getStatus() == ReservationStatus.COMPLETE ||
                    (r.getStatus() == ReservationStatus.TEMP && r.getExpiredAt().isAfter(LocalDateTime.now()))
                );
    }

    public ReservationResult findReserve(Long reserveId) {
        return ReservationResult.from(reservationRepository.findReserve(reserveId));
    }

}
