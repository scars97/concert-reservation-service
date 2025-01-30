package com.hhconcert.server.interfaces.api.queues;

import com.hhconcert.server.application.facade.QueueFacade;
import com.hhconcert.server.global.common.model.ErrorResponse;
import com.hhconcert.server.interfaces.api.queues.dto.TokenRequest;
import com.hhconcert.server.interfaces.api.queues.dto.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created - WAIT 토큰 생성",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = TokenResponse.class)
            )
        ),
        @ApiResponse(responseCode = "409", description = "Conflict - 토큰 중복 발급",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "status": "CONFLICT", 
                        "message": "이미 토큰이 존재합니다."
                    }
                """)
            )
        )
    })
    @PostMapping("")
    public ResponseEntity<TokenResponse> createQueueToken(@Valid @RequestBody TokenRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(TokenResponse.from(queueFacade.createToken(request)));
    }

    @Operation(summary = "대기열 상태 요청", security = @SecurityRequirement(name = "queue-token"))
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = TokenResponse.class),
            examples = {
                @ExampleObject(name = "WAIT 토큰", value = """
                    {
                        "tokenId": "15e859ae-9bf2-4f08-ae68-465e9dcd54bf",
                        "userId": "user1234",
                        "priority": 10,
                        "status": "WAIT",
                        "tokenIssuedAt": "2025-01-30T11:50:000Z",
                        "activeAt": null,
                        "expireAt": null
                    }
                """),
                @ExampleObject(name = "ACTIVE 토큰", value = """
                    {
                        "tokenId": "15e859ae-9bf2-4f08-ae68-465e9dcd54bf",
                        "userId": "user1234",
                        "priority": 0,
                        "status": "ACTIVE",
                        "tokenIssuedAt": "2025-01-30T11:50:000Z",
                        "activeAt": "2025-01-30T11:58:000Z",
                        "expireAt": "2025-01-30T12:03:000Z"
                    }
                """)
            }
        )
    )
    @GetMapping("/{userId}")
    public ResponseEntity<TokenResponse> getQueueStatus(@Valid TokenRequest request) {
        return ResponseEntity.ok(TokenResponse.from(queueFacade.checkQueueStatus(request)));
    }

}
