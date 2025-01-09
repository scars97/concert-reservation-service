package com.hhconcert.server.interfaces.api.reservation;

import com.hhconcert.server.interfaces.api.reservation.dto.ReservationRequest;
import com.hhconcert.server.interfaces.api.reservation.dto.ReservationResponse;
import com.hhconcert.server.interfaces.facade.ReservationFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    @PostMapping("")
    public ResponseEntity<ReservationResponse> createTempReserve(@RequestBody ReservationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationFacade.tempReserve(request));
    }

}
