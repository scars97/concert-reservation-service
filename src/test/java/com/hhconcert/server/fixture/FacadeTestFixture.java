package com.hhconcert.server.fixture;

import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.payment.entity.Payment;
import com.hhconcert.server.business.domain.payment.entity.PaymentStatus;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.reservation.entity.ReservationStatus;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.infrastructure.concert.ConcertJpaRepository;
import com.hhconcert.server.infrastructure.payment.PaymentJpaRepository;
import com.hhconcert.server.infrastructure.reservation.ReservationJpaRepository;
import com.hhconcert.server.infrastructure.reservation.ReservationRedisRepository;
import com.hhconcert.server.infrastructure.schedule.ScheduleJpaRepository;
import com.hhconcert.server.infrastructure.seat.SeatJpaRepository;
import com.hhconcert.server.infrastructure.user.UserJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class FacadeTestFixture {

    @Autowired
    private ConcertJpaRepository concertJpaRepository;
    @Autowired
    private ScheduleJpaRepository scheduleJpaRepository;
    @Autowired
    private SeatJpaRepository seatJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;
    @Autowired
    private ReservationJpaRepository reservationJpaRepository;
    @Autowired
    private ReservationRedisRepository reservationRedisRepository;
    @Autowired
    private PaymentJpaRepository paymentJpaRepository;

    public void concertFixture(LocalDate nowDate, LocalDateTime nowTime, long currentTime) {
        Concert saveConcert1 = concertJpaRepository.save(new Concert("콘서트1", nowDate, nowDate.plusDays(1)));
        Concert saveConcert2 = concertJpaRepository.save(new Concert("콘서트2", nowDate.plusDays(1), nowDate.plusDays(2)));

        Schedule saveSchedule1 = scheduleJpaRepository.save(new Schedule(saveConcert1,nowDate));
        Schedule saveSchedule2 = scheduleJpaRepository.save(new Schedule(saveConcert1,nowDate.plusDays(1)));

        Seat seat1 = seatJpaRepository.save(new Seat(saveSchedule1, "A1", 75000));
        Seat seat2 = seatJpaRepository.save(new Seat(saveSchedule1, "B1", 60000));
        Seat seat3 = seatJpaRepository.save(new Seat(saveSchedule1, "C1", 5000));

        User user1 = userJpaRepository.save(new User("test1234", 80000));
        User user2 = userJpaRepository.save(new User("qwer1234", 60000));
        User user3 = userJpaRepository.save(new User("asdf1234", 40000));

        Reservation saveReserve1 = reservationJpaRepository.save(new Reservation(user1, saveConcert1, saveSchedule1, seat1, seat1.getPrice(), ReservationStatus.TEMP, nowTime.plusMinutes(5)));
        Reservation saveReserve2 = reservationJpaRepository.save(new Reservation(user1, saveConcert1, saveSchedule1, seat1, seat1.getPrice(), ReservationStatus.CANCEL, nowTime.minusMinutes(6)));
        Reservation saveReserve3 = reservationJpaRepository.save(new Reservation(user2, saveConcert1, saveSchedule1, seat2, seat2.getPrice(), ReservationStatus.TEMP, nowTime.minusMinutes(3)));
        Reservation saveReserve4 = reservationJpaRepository.save(new Reservation(user3, saveConcert1, saveSchedule1, seat3, seat3.getPrice(), ReservationStatus.COMPLETE, nowTime.minusMinutes(2)));

        // 예약 만료 스케줄러로 인해 2번 좌석 삭제.
        reservationRedisRepository.zSetAdd(1L, currentTime + 5000);
        //reservationRedisRepository.zSetAdd(2L, currentTime - 3000);
        reservationRedisRepository.zSetAdd(3L, 0L);

        paymentJpaRepository.save(new Payment(user1, saveReserve2, seat1.getPrice(), PaymentStatus.CANCEL));
        paymentJpaRepository.save(new Payment(user3, saveReserve4, seat3.getPrice(), PaymentStatus.SUCCESS));
    }

    public void paymentFixture(LocalDate nowDate, LocalDateTime nowTime) {
        Concert saveConcert1 = concertJpaRepository.save(new Concert("콘서트1", nowDate, nowDate.plusDays(1)));
        Concert saveConcert2 = concertJpaRepository.save(new Concert("콘서트2", nowDate.plusDays(1), nowDate.plusDays(2)));

        Schedule saveSchedule1 = scheduleJpaRepository.save(new Schedule(saveConcert1, nowDate));
        Schedule saveSchedule2 = scheduleJpaRepository.save(new Schedule(saveConcert1, nowDate.plusDays(1)));

        Seat seat1 = seatJpaRepository.save(new Seat(saveSchedule1, "A1", 75000));
        Seat seat2 = seatJpaRepository.save(new Seat(saveSchedule1, "B1", 60000));
        Seat seat3 = seatJpaRepository.save(new Seat(saveSchedule1, "C1", 5000));

        User user1 = userJpaRepository.save(new User("test1234", 80000));
        User user2 = userJpaRepository.save(new User("qwer1234", 60000));
        User user3 = userJpaRepository.save(new User("asdf1234", 40000));

        Reservation saveReserve1 = reservationJpaRepository.save(new Reservation(user1, saveConcert1, saveSchedule1, seat1, seat1.getPrice(), ReservationStatus.TEMP, nowTime.plusMinutes(5)));
    }

    public void reservationFixture(LocalDate nowDate) {
        Concert saveConcert1 = concertJpaRepository.save(new Concert("콘서트1", nowDate, nowDate.plusDays(1)));
        Schedule saveSchedule1 = scheduleJpaRepository.save(new Schedule(saveConcert1, nowDate));
        Seat seat1 = seatJpaRepository.save(new Seat(saveSchedule1, "A1", 75000));
        User user1 = userJpaRepository.save(new User("test1234", 80000));
    }

}
