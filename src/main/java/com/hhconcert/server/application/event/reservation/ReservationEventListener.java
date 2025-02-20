package com.hhconcert.server.application.event.reservation;

import com.hhconcert.server.application.event.DataPlatformService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationEventListener {

    private final DataPlatformService dataPlatformService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void sendToDataPlatform(ReserveSuccessEvent event) {
        try {
            log.info("Send Reservation Data for reserveId : {}", event.reserveId());
            dataPlatformService.send(event.reserveId());
        } catch (Exception e) {
            log.error("Failed Send for DataPlatform : {}", e.getMessage());
        }
    }

}
