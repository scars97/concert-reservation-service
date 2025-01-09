package com.hhconcert.server.interfaces.facade;

import com.hhconcert.server.business.domain.payment.service.PaymentService;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentRequest;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentFacade {

    private final PaymentService paymentService;

    public PaymentResponse payment(PaymentRequest request) {
        return PaymentResponse.from(paymentService.payment(request.toInfo()));
    }

}
