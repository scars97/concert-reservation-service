package com.hhconcert.server.business.domain.reservation.entity;

import com.hhconcert.server.business.domain.common.BaseEntity;
import com.hhconcert.server.business.domain.concert.entity.Concert;
import com.hhconcert.server.business.domain.concert.entity.Schedule;
import com.hhconcert.server.business.domain.concert.entity.Seat;
import com.hhconcert.server.business.domain.user.entity.User;
import com.hhconcert.server.global.exception.ErrorCode;
import com.hhconcert.server.global.exception.BusinessException;
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
    @JoinColumn(name = "user_seq", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Concert concert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Schedule schedule;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Seat seat;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private LocalDateTime expiredAt;

    public Reservation(User user, Concert concert, Schedule schedule, Seat seat, Integer price, ReservationStatus status, LocalDateTime expiredAt) {
        this.user = user;
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

    public boolean isNotMatchAmount(int amount) {
        return this.price != amount;
    }

    public void updateForComplete() {
        if (this.status != ReservationStatus.TEMP) {
            throw new BusinessException(ErrorCode.INVALID_RESERVATION_STATUS);
        }
        if (this.expiredAt.isBefore(LocalDateTime.now())) {
            throw new BusinessException(ErrorCode.EXPIRED_RESERVATION);
        }
        this.status = ReservationStatus.COMPLETE;
        this.expiredAt = null;
    }

}
