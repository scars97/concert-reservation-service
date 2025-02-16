package com.hhconcert.server.application.event;

public record ReserveSuccessEvent(
        Long reserveId
) {
    public static ReserveSuccessEvent of(Long reserveId) {
        return new ReserveSuccessEvent(reserveId);
    }
}
