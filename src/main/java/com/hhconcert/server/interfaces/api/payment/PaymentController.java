package com.hhconcert.server.interfaces.api.payment;

import com.hhconcert.server.interfaces.api.payment.dto.PaymentRequest;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentResponse;
import com.hhconcert.server.application.facade.PaymentFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "결제 API")
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @Operation(summary = "예약건 결제", security = @SecurityRequirement(name = "queue-token"))
    @PostMapping("")
    public ResponseEntity<PaymentResponse> payment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PaymentResponse.from(paymentFacade.payment(request)));
    }

}
