package com.hhconcert.server.application.event.reservation;

import com.hhconcert.server.business.reservation.dto.ReserveSuccessEvent;
import com.hhconcert.server.business.reservation.service.ReservationOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ReservationOutboxService outboxService;

    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void initOutBox(ReserveSuccessEvent event) {
        outboxService.init(event);
        log.info("init outbox : {}", event.reserveId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendReservationMessage(ReserveSuccessEvent event) {
        kafkaTemplate.send("reserve-notification", event);
        log.info("send message : {}", event.reserveId());
    }
}
