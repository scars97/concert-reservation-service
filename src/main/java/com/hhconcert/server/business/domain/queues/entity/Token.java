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
@Table(
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id"}),
    indexes = @Index(name = "idx_status", columnList = "status")
)
public class Token extends BaseEntity {

    @Id
    private String tokenId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private TokenStatus status;

    private LocalDateTime activeAt;

    private LocalDateTime expiredAt;

    private LocalDateTime tokenIssuedAt;

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
                .tokenIssuedAt(LocalDateTime.now())
                .build();
    }

    public static Token createForActive(String userId) {
        return Token.builder()
                .tokenId(TokenGenerator.generateToken(userId))
                .userId(userId)
                .status(TokenStatus.ACTIVE)
                .activeAt(LocalDateTime.now())
                .expiredAt(LocalDateTime.now().plusMinutes(5))
                .tokenIssuedAt(LocalDateTime.now())
                .build();
    }

    public void activeForMinutes(int minute) {
        this.status = TokenStatus.ACTIVE;
        this.activeAt = LocalDateTime.now();
        this.expiredAt = this.activeAt.plusMinutes(minute);
    }

}
