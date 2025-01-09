package com.hhconcert.server.infrastructure.payment;

import com.hhconcert.server.business.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
