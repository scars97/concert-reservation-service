package com.hhconcert.server.business.domain.payment.persistance;

import com.hhconcert.server.business.domain.payment.entity.Payment;

public interface PaymentRepository {

    /**
     * 예약건 결제
     * @param payment
     * @return
     */
    Payment payment(Payment payment);

}
