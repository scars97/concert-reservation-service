package com.hhconcert.server.application.event;

import org.springframework.stereotype.Component;

@Component
public interface ReservationEventPublisher {

    void publish(ReserveSuccessEvent event);

}
