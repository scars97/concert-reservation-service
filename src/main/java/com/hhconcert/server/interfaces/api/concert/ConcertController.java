package com.hhconcert.server.interfaces.api.concert;

import com.hhconcert.server.interfaces.api.concert.dto.ConcertResponse;
import com.hhconcert.server.interfaces.api.concert.dto.ConcertScheduleResponse;
import com.hhconcert.server.interfaces.api.concert.dto.ScheduleSeatResponse;
import com.hhconcert.server.interfaces.facade.ConcertFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @GetMapping("")
    public ResponseEntity<List<ConcertResponse>> getConcerts() {
        return ResponseEntity.ok(concertFacade.getConcerts());
    }

    @Operation(summary = "콘서트 상세 조회")
    @GetMapping("/{concertId}")
    public ResponseEntity<ConcertResponse> findConcert(@PathVariable("concertId") Long concertId) {
        return ResponseEntity.ok(concertFacade.findConcert(concertId));
    }

    @Operation(summary = "콘서트 예약 가능 날짜 조회", security = @SecurityRequirement(name = "queue-token"))
    @GetMapping("/{concertId}/schedules")
    public ResponseEntity<ConcertScheduleResponse> findConcertSchedule(@PathVariable("concertId") Long concertId) {
        return ResponseEntity.ok(concertFacade.getSchedules(concertId));
    }

    @Operation(summary = "콘서트 예약 가능 좌석 조회", security = @SecurityRequirement(name = "queue-token"))
    @GetMapping("/schedules/{scheduleId}/seats")
    public ResponseEntity<ScheduleSeatResponse> getAvailableSeats(@PathVariable("scheduleId") Long scheduleId) {
        return ResponseEntity.ok(concertFacade.getAvailableSeats(scheduleId));
    }

}
