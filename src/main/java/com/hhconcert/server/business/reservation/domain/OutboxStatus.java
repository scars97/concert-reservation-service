package com.hhconcert.server.business.reservation.domain;

import lombok.Getter;

@Getter
public enum OutboxStatus {
    INIT,
    PUBLISHED
}
