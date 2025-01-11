package com.hhconcert.server.infrastructure.payment;

import com.hhconcert.server.business.domain.payment.entity.Payment;
import com.hhconcert.server.business.domain.payment.persistance.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentCoreRepository implements PaymentRepository {

    private final PaymentJpaRepository repository;

    @Override
    public Payment payment(Payment payment) {
        return repository.save(payment);
    }

}
