package com.hhconcert.server.business.domain.seat.entity;

import com.hhconcert.server.business.domain.BaseEntity;
import com.hhconcert.server.business.domain.schedule.entity.Schedule;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "seat_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Schedule schedule;

    @Column(nullable = false)
    private String seatNumber;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    private SeatStatus status;

    public Seat(Schedule schedule, String seatNumber, Integer price, SeatStatus status) {
        this.schedule = schedule;
        this.seatNumber = seatNumber;
        this.price = price;
        this.status = status;
    }
}
