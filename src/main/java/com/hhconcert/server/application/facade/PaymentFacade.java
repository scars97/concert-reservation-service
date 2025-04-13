package com.hhconcert.server.application.facade;

import com.hhconcert.server.application.dto.PaymentResult;
import com.hhconcert.server.application.event.payment.PaymentSuccessEvent;
import com.hhconcert.server.business.payment.dto.PaymentInfo;
import com.hhconcert.server.business.payment.service.PaymentService;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Transactional
    public PaymentResult payment(PaymentRequest request) {

        PaymentInfo payment = paymentService.payment(request.toCommand());

        applicationEventPublisher.publishEvent(new PaymentSuccessEvent(request.userId()));

        return PaymentResult.from(payment);
    }

}
