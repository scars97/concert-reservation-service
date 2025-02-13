package com.hhconcert.server.application.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReservationEventPublisherImpl implements ReservationEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(ReserveSuccessEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

}
