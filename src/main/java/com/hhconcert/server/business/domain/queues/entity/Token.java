package com.hhconcert.server.business.domain.queues.entity;

import com.hhconcert.server.business.domain.BaseEntity;
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
public class Token extends BaseEntity {

    @Id
    private String tokenId;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TokenStatus status;

    private Integer priority;

    private LocalDateTime activeAt;

    private LocalDateTime expiredAt;

    public Token(String tokenId, String userId, TokenStatus status, Integer priority) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.status = status;
        this.priority = priority;
    }

    public static Token createForWait(String userId, Integer priority) {
        return Token.builder()
                .tokenId(TokenGenerator.generateToken(userId))
                .userId(userId)
                .status(TokenStatus.WAIT)
                .priority(priority)
                .build();
    }

    public static Token createForActive(String userId) {
        return Token.builder()
                .tokenId(TokenGenerator.generateToken(userId))
                .userId(userId)
                .status(TokenStatus.ACTIVE)
                .activeAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .build();
    }

    public void activeForMinutes(int minute) {
        this.status = TokenStatus.ACTIVE;
        this.activeAt = LocalDateTime.now();
        this.expiredAt = this.activeAt.plusMinutes(minute);
    }

}
