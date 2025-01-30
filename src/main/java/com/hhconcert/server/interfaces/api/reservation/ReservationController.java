package com.hhconcert.server.interfaces.api.reservation;

import com.hhconcert.server.application.facade.ReservationFacade;
import com.hhconcert.server.global.common.model.ErrorResponse;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationRequest;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationResponse;
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

@Tag(name = "예약 API")
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationFacade reservationFacade;

    @Operation(summary = "콘서트 좌석 임시 예약", security = @SecurityRequirement(name = "queue-token"))
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Created - 임시 예약 생성",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ReservationResponse.class)
            )
        ),
        @ApiResponse(responseCode = "409", description = "Conflict - 예약된 좌석 요청",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "status": "CONFLICT", 
                        "message": "이미 예약된 좌석입니다."
                    }
                """)
            )
        )
    })
    @PostMapping("")
    public ResponseEntity<ReservationResponse> createTempReserve(@Valid @RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ReservationResponse.from(reservationFacade.tempReserve(request)));
    }

}
