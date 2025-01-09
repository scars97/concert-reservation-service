package com.hhconcert.server.business.domain.reservation.entity;

import com.hhconcert.server.business.domain.BaseEntity;
import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import com.hhconcert.server.business.domain.seat.entity.Seat;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.global.exception.ReservationException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reserve_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id")
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private Integer price;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime expiredAt;

    public Reservation(Concert concert, Schedule schedule, Seat seat, Integer price, ReservationStatus status, LocalDateTime expiredAt) {
        this.concert = concert;
        this.schedule = schedule;
        this.seat = seat;
        this.price = price;
        this.status = status;
        this.expiredAt = expiredAt;
    }

    public static Reservation createTemp(User user, Concert concert, Schedule schedule, Seat seat) {
        return Reservation.builder()
                .user(user)
                .concert(concert)
                .schedule(schedule)
                .seat(seat)
                .price(seat.getPrice())
                .status(ReservationStatus.TEMP)
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .build();
    }

    public void updateForComplete() {
        if (this.status != ReservationStatus.TEMP) {
            throw new ReservationException("결제할 수 없는 예약 내역입니다.");
        }
        this.status = ReservationStatus.COMPLETE;
    }
}
