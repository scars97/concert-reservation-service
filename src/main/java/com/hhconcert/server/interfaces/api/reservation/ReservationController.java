package com.hhconcert.server.interfaces.api.reservation;

import com.hhconcert.server.exception.TokenException;
import com.hhconcert.server.exception.UnAuthorizationException;
import com.hhconcert.server.interfaces.api.concert.dto.ConcertResponse;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationRequest;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    @PostMapping("")
    public ResponseEntity<ReservationResponse> createReservation(
            @RequestHeader HttpHeaders headers,
            @RequestBody ReservationRequest request) {
        String token = headers.getFirst("Authorization");
        if (token == null) {
            throw new UnAuthorizationException("토큰 정보가 누락되었습니다.");
        }

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token.substring(7);
        } else {
            throw new TokenException("잘못된 토큰입니다.");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ReservationResponse(
                1L,
                    LocalDate.of(2025,1,1),
                    "A1",
                    new ConcertResponse(1L, "콘서트명1", LocalDate.of(2024,12,31), LocalDate.of(2025,1,1)),
                    75000,
                    "TEMP",
                    LocalDateTime.of(2024,12,30,0,5).withSecond(0).withNano(1),
                    LocalDateTime.of(2024,12,30,0,10).withSecond(0).withNano(1)
            )
        );
    }
}
