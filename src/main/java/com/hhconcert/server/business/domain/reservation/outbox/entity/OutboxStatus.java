package com.hhconcert.server.business.domain.reservation.outbox.entity;

import lombok.Getter;

@Getter
public enum OutboxStatus {
    INIT,
    PUBLISHED
}
