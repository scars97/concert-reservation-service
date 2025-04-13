package com.hhconcert.server.application.event.payment;

import com.hhconcert.server.business.queues.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventListener {

    private final TokenService tokenService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void afterPaymentThenExpireToken(PaymentSuccessEvent event) {
        try {
            tokenService.dropTokenBy(event.userId());
            log.info("Expire Token for userId : {}", event.userId());
        } catch (Exception e) {
            log.error("Failed to Expire Token for userId : {}", event.userId());
        }
    }
}
