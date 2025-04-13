package com.hhconcert.server.business.payment.persistance;

import com.hhconcert.server.business.payment.domain.Payment;

public interface PaymentRepository {

    /**
     * 예약건 결제
     * @param payment
     * @return
     */
    Payment payment(Payment payment);

}
