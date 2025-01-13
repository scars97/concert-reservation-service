package com.hhconcert.server.business.domain.user.entity;

import com.hhconcert.server.business.domain.BaseEntity;
import com.hhconcert.server.global.common.exception.PointException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_seq")
    private Long id;

    @Column(name = "user_id")
    private String userId;

    private Integer point;

    public User(String userId, Integer point) {
        this.userId = userId;
        this.point = point;
    }

    public void chargePoint(Integer amount) {
        this.point += amount;
    }

    public void usePoint(Integer amount) {
        if (this.point < amount) {
            throw new PointException("잔액이 부족합니다.");
        }
        this.point -= amount;
    }
}
