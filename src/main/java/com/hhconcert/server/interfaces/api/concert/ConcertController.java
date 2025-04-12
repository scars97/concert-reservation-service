package com.hhconcert.server.interfaces.api.concert;

import com.hhconcert.server.application.facade.ConcertFacade;
import com.hhconcert.server.global.model.ErrorResponse;
import com.hhconcert.server.interfaces.api.concert.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "콘서트 API", description = "콘서트, 날짜, 좌석 조회")
@RestController
@RequestMapping("/concerts")
@RequiredArgsConstructor
public class ConcertController {

    private final ConcertFacade concertFacade;

    @Operation(summary = "콘서트 목록 조회")
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(
            mediaType = "application/json",
            array = @ArraySchema(schema = @Schema(implementation = ConcertResponse.class))
        )
    )
    @GetMapping("")
    public ResponseEntity<List<ConcertResponse>> getConcerts() {
        return ResponseEntity.ok(concertFacade.getConcerts()
                .stream()
                .map(ConcertResponse::from)
                .toList());
    }

    @Operation(summary = "콘서트 상세 조회")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "OK",
            content = @Content(
                schema = @Schema(implementation = ConcertResponse.class),
                mediaType = "application/json"
            )
        ),
        @ApiResponse(responseCode = "404", description = "Not Found",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                    {
                        "status": 404, 
                        "message": "등록되지 않은 콘서트입니다."
                    }
                """)
            )
        )
    })
    @GetMapping("/{concertId}")
    public ResponseEntity<ConcertResponse> findConcert(@Valid ConcertRequest request) {
        return ResponseEntity.ok(ConcertResponse.from(concertFacade.findConcert(request)));
    }

    @Operation(summary = "콘서트 예약 가능 날짜 조회", security = @SecurityRequirement(name = "queue-token"))
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ConcertScheduleResponse.class)
        )
    )
    @GetMapping("/{concertId}/schedules")
    public ResponseEntity<ConcertScheduleResponse> findConcertSchedule(@Valid ConcertRequest request) {
        List<ScheduleResponse> schedules = concertFacade.getSchedules(request)
                .stream()
                .map(ScheduleResponse::from)
                .toList();

        return ResponseEntity.ok(ConcertScheduleResponse.from(request.concertId(), schedules));
    }

    @Operation(summary = "콘서트 예약 가능 좌석 조회", security = @SecurityRequirement(name = "queue-token"))
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = ScheduleSeatResponse.class)
        )
    )
    @GetMapping("/schedules/{scheduleId}/seats")
    public ResponseEntity<ScheduleSeatResponse> getAvailableSeats(@Valid ScheduleRequest request) {
        List<SeatResponse> seats = concertFacade.getAvailableSeats(request)
                .stream()
                .map(SeatResponse::from)
                .toList();

        return ResponseEntity.ok(ScheduleSeatResponse.from(request.scheduleId(), seats));
    }

}
