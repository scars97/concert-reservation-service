package com.hhconcert.server.infrastructure.persistence.core;

import com.hhconcert.server.business.domain.payment.entity.Payment;
import com.hhconcert.server.business.domain.payment.persistance.PaymentRepository;
import com.hhconcert.server.infrastructure.persistence.jpa.PaymentJpaRepository;
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
