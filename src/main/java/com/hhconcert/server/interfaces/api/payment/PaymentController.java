package com.hhconcert.server.interfaces.api.payment;

import com.hhconcert.server.exception.PaymentException;
import com.hhconcert.server.exception.TokenException;
import com.hhconcert.server.exception.UnAuthorizationException;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentRequest;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/payments")
public class PaymentController {

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
                "COMPLETE",
                LocalDateTime.of(2024,12,30,0,13, 0, 1)
            )
        );
    }
}