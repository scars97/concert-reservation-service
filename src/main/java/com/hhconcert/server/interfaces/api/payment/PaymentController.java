package com.hhconcert.server.interfaces.api.payment;

import com.hhconcert.server.application.facade.PaymentFacade;
import com.hhconcert.server.global.common.model.ErrorResponse;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentRequest;
import com.hhconcert.server.interfaces.api.payment.dto.PaymentResponse;
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
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created - 결제 내역 생성",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = PaymentResponse.class)
            )
        ),
        @ApiResponse(responseCode = "400", description = "Bad Request - 잘못된 금액 요청",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "status": "BAD_REQUEST", 
                        "message": "결제 금액이 일치하지 않습니다."
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "402", description = "Payment Required - 보유 포인트 부족",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "status": "PAYMENT_REQUIRED", 
                        "message": "잔액이 부족합니다."
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "409", description = "Conflict - 결제할 수 없는 예약 상태(COMPLETE, CANCEL)",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "status": "CONFLICT", 
                        "message": "결제할 수 없는 예약 내역입니다."
                    }
                """)
            )
        ),
        @ApiResponse(responseCode = "410", description = "Gone - 임시 예약 시간 만료",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "status": "GONE", 
                        "message": "임시 예약 시간이 만료되었습니다."
                    }
                """)
            )
        ),
    })
    @PostMapping("")
    public ResponseEntity<PaymentResponse> payment(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(PaymentResponse.from(paymentFacade.payment(request)));
    }

}
