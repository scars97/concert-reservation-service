package com.hhconcert.server.business.domain.reservation.outbox.service;

import com.hhconcert.server.business.domain.reservation.outbox.dto.ReserveSuccessEvent;
import com.hhconcert.server.business.domain.reservation.outbox.entity.ReservationOutbox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@EnableScheduling
@Component
@RequiredArgsConstructor
public class ReservationOutboxScheduler {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ReservationOutboxService outboxService;

    @Scheduled(cron = "0 0/5 * * * *")
    public void rePublished() {
        log.info("Outbox 스케줄러 실행");
        
        List<ReservationOutbox> outboxes = outboxService.findInitOutboxOlderThan(5);

        if (!outboxes.isEmpty()) {
            outboxes.forEach(outbox -> {
                ReserveSuccessEvent event = outbox.toReserveSuccessEvent();

                try {
                    kafkaTemplate.send("reserve-notification", event);
                } catch (Exception e) {
                    log.error("Failed Reservation rePublished : {}", event.reserveId());
                }
            });
        }
        
        log.info("Outbox 스케줄러 종료");
    }

}
