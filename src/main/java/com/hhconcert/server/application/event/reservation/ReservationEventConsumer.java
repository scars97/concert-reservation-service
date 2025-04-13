package com.hhconcert.server.application.event.reservation;

import com.hhconcert.server.application.event.DataPlatformService;
import com.hhconcert.server.business.reservation.dto.ReserveSuccessEvent;
import com.hhconcert.server.business.reservation.service.ReservationOutboxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventConsumer {

    private final ReservationOutboxService outboxService;
    private final DataPlatformService dataPlatformService;

    @KafkaListener(
        topics = "reserve-notification",
        groupId = "RESERVATION-CONSUMER-GROUP",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void sendToDataPlatform(ReserveSuccessEvent event) {
        try {
            outboxService.updateForPublished(event);
            log.info("published outbox : {}", event.reserveId());

            dataPlatformService.send(event.reserveId());
            log.info("Send Reservation Data for reserveId : {}", event.reserveId());
        } catch (Exception e) {
            log.error("Failed Send for DataPlatform : {}", e.getMessage());
        }
    }

}
