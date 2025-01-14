package com.hhconcert.server.interfaces.mockapi;

import com.hhconcert.server.business.domain.payment.entity.PaymentStatus;
import com.hhconcert.server.business.domain.payment.exception.PaymentException;
import com.hhconcert.server.business.domain.queues.exception.TokenException;
import com.hhconcert.server.global.common.exception.definitions.UnAuthorizationException;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentRequest;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "결제 API")
@RestController
@RequestMapping("/mock/payments")
public class MockPaymentController {

    @Operation(summary = "예약건 결제", security = @SecurityRequirement(name = "queue-token"))
    @PostMapping("")
    public ResponseEntity<PaymentResponse> payment(
            @RequestHeader HttpHeaders headers,
            @RequestBody PaymentRequest request) {
        String token = headers.getFirst("Authorization");
        if (token == null) {
            throw new UnAuthorizationException("토큰 정보가 누락되었습니다.");
        }

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token.substring(7);
        } else {
            throw new TokenException("잘못된 토큰입니다.");
        }

        if (request.amount() < 75000) {
            throw new PaymentException("포인트가 부족합니다.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new PaymentResponse(
                1L,
                request.reserveId(),
                request.userId(),
                request.amount(),
                PaymentStatus.SUCCESS,
                LocalDateTime.of(2024,12,30,0,13, 0, 1)
            )
        );
    }
}
