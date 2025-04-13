package com.hhconcert.server.business.domain.concert.entity;

import com.hhconcert.server.business.domain.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(indexes = {
    @Index(name = "idx_schedule", columnList = "schedule_id")
})
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

    public Seat(Schedule schedule, String seatNumber, Integer price) {
        this.schedule = schedule;
        this.seatNumber = seatNumber;
        this.price = price;
    }
}
