package com.hhconcert.server.application.facade;

import com.hhconcert.server.application.dto.PaymentResult;
import com.hhconcert.server.business.domain.payment.service.PaymentService;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;

    public PaymentResult payment(PaymentRequest request) {
        return PaymentResult.from(paymentService.payment(request.toCommand()));
    }

}
