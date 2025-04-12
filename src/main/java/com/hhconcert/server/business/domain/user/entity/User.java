package com.hhconcert.server.business.domain.user.entity;

import com.hhconcert.server.business.domain.common.BaseEntity;
import com.hhconcert.server.global.exception.ErrorCode;
import com.hhconcert.server.global.exception.BusinessException;
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

    @Column(name = "user_id", unique = true, nullable = false)
    private String userId;

    private Integer point;

    @Version
    private Long version;

    public User(Long id, String userId, Integer point) {
        this.id = id;
        this.userId = userId;
        this.point = point;
    }

    public User(String userId, Integer point) {
        this.userId = userId;
        this.point = point;
    }

    public void chargePoint(Integer amount) {
        this.point += amount;
    }

    public void usePoint(Integer amount) {
        if (this.point < amount) {
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINTS);
        }
        this.point -= amount;
    }
}
