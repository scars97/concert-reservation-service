package com.hhconcert.server.application;

import com.hhconcert.server.application.dto.PaymentResult;
import com.hhconcert.server.application.facade.PaymentFacade;
import com.hhconcert.server.business.payment.domain.PaymentStatus;
import com.hhconcert.server.config.IntegrationTestSupport;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentRequest;
import com.hhconcert.server.fixture.FacadeTestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentFacadeTest extends IntegrationTestSupport {

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private FacadeTestFixture fixture;

    @BeforeEach
    void setUp() {
        LocalDate nowDate = LocalDate.now();
        LocalDateTime nowTime = LocalDateTime.now().withNano(0);

        fixture.paymentFixture(nowDate, nowTime);
    }

    @DisplayName("예약건에 대한 결제가 완료된다.")
    @Test
    void payment() {
        PaymentResult result = paymentFacade.payment(new PaymentRequest("test1234", 1L, 75000));

        assertThat(result)
                .extracting("paymentId", "price", "status")
                .containsExactly(1L, 75000, PaymentStatus.SUCCESS);
    }

}