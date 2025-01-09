package com.hhconcert.server.interfaces.api.queues;

import com.hhconcert.server.interfaces.api.queues.dto.TokenRequest;
import com.hhconcert.server.interfaces.api.queues.dto.TokenResponse;
import com.hhconcert.server.interfaces.facade.QueueFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "대기열 API", description = "토큰 발급 및 대기열 상태 요청")
@RestController
@RequestMapping("/queues")
@RequiredArgsConstructor
public class QueueController {

    private final QueueFacade queueFacade;

    @Operation(summary = "토큰 발급")
    @PostMapping("")
    public ResponseEntity<TokenResponse> createQueueToken(@RequestBody TokenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(queueFacade.createToken(request));
    }

    @Operation(summary = "대기열 상태 요청", security = @SecurityRequirement(name = "queue-token"))
    @GetMapping("/{userId}")
    public ResponseEntity<TokenResponse> getQueueStatus(@PathVariable("userId") String userId) {
        TokenResponse response = queueFacade.checkQueueStatus(userId);
        return ResponseEntity.ok(response);
    }

}
