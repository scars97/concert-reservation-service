package com.hhconcert.server.application.event;

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
    public void handle(ReserveSuccessEvent event) {
        try {
            log.info("예약 데이터 전송 : {}", event.reserveId());
            dataPlatformService.send(event.reserveId());
        } catch (Exception e) {
            log.error("데이터 플랫폼 전송 실패 : {}", e.getMessage());
        }
    }

}
