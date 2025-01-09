package com.hhconcert.server.business.domain.payment.entity;

import com.hhconcert.server.business.domain.BaseEntity;
import com.hhconcert.server.business.domain.reservation.entity.Reservation;
import com.hhconcert.server.business.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserve_id")
    private Reservation reservation;

    @Column(nullable = false)
    private Integer price;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @Builder
    public Payment(User user, Reservation reservation, Integer price, PaymentStatus status) {
        this.user = user;
        this.reservation = reservation;
        this.price = price;
        this.status = status;
    }

    public static Payment create(User user, Reservation reservation, Integer price) {
        return Payment.builder()
                .user(user)
                .reservation(reservation)
                .price(price)
                .status(PaymentStatus.SUCCESS)
                .build();
    }
}
