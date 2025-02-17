package com.hhconcert.server.application.event.reservation;

public record ReserveSuccessEvent(
        Long reserveId
) {
    public static ReserveSuccessEvent of(Long reserveId) {
        return new ReserveSuccessEvent(reserveId);
    }
}
