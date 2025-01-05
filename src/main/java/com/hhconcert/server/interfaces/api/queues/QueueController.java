package com.hhconcert.server.interfaces.api.queues;

import com.hhconcert.server.global.exception.TokenException;
import com.hhconcert.server.global.exception.UnAuthorizationException;
import com.hhconcert.server.interfaces.api.queues.dto.TokenRequest;
import com.hhconcert.server.interfaces.api.queues.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "대기열 API", description = "토큰 발급 및 대기열 상태 요청")
@RestController
@RequestMapping("/queues")
public class QueueController {

    @Operation(summary = "토큰 발급")
    @PostMapping("/token")
    public ResponseEntity<TokenResponse> createQueueToken(@RequestBody TokenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new TokenResponse(UUID.randomUUID().toString(), request.userId(), request.concertId(), 34, "WAIT")
        );
    }

    @Operation(summary = "대기열 상태 요청", security = @SecurityRequirement(name = "queue-token"))
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
