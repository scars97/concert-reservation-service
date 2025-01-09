package com.hhconcert.server.business.domain.user.entity;

import com.hhconcert.server.business.domain.BaseEntity;
import com.hhconcert.server.global.exception.PointException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @Column(name = "user_id")
    private String id;

    private Integer point;

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
