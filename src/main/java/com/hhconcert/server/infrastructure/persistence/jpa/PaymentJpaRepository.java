package com.hhconcert.server.infrastructure.persistence.jpa;

import com.hhconcert.server.business.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
