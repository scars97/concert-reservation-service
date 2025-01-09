package com.hhconcert.server.business.domain.queues.entity;

import com.hhconcert.server.business.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

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

    private LocalDateTime activeAt;

    private LocalDateTime expiredAt;

    public Token(String tokenId, String userId, TokenStatus status) {
        this.tokenId = tokenId;
        this.userId = userId;
        this.status = status;
    }

    public static Token createForWait(String userId) {
        return Token.builder()
                .tokenId(TokenGenerator.generateToken(userId))
                .userId(userId)
                .status(TokenStatus.WAIT)
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
