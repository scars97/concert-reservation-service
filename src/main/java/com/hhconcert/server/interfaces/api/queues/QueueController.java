package com.hhconcert.server.interfaces.api.queues;

import com.hhconcert.server.exception.TokenException;
import com.hhconcert.server.exception.UnAuthorizationException;
import com.hhconcert.server.interfaces.api.queues.dto.TokenRequest;
import com.hhconcert.server.interfaces.api.queues.dto.TokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/queues")
public class QueueController {

    @PostMapping("/token")
    public ResponseEntity<TokenResponse> createQueueToken(@RequestBody TokenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new TokenResponse(UUID.randomUUID().toString(), request.userId(), request.concertId(), 34, "WAIT")
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<TokenResponse> getQueueStatus(
            @RequestHeader HttpHeaders header,
            @PathVariable("userId") String userId) {

        String token = header.getFirst("Authorization");
        if (token == null) {
            throw new UnAuthorizationException("토큰 정보가 누락되었습니다.");
        }

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            throw new TokenException("유효하지 않은 토큰입니다.");
        }

        return ResponseEntity.ok(
                new TokenResponse(token, userId, 1L, 0, "ACTIVE")
        );
    }
}
