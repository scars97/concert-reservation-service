package com.hhconcert.server.interfaces.api.concert;

import com.hhconcert.server.global.exception.ConcertException;
import com.hhconcert.server.global.exception.TokenException;
import com.hhconcert.server.global.exception.UnAuthorizationException;
import com.hhconcert.server.interfaces.api.concert.dto.ConcertResponse;
import com.hhconcert.server.interfaces.api.concert.dto.ConcertScheduleResponse;
import com.hhconcert.server.interfaces.api.concert.dto.ScheduleResponse;
import com.hhconcert.server.interfaces.api.concert.dto.ScheduleSeatResponse;
import com.hhconcert.server.interfaces.api.concert.dto.SeatResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "콘서트 API", description = "콘서트, 날짜, 좌석 조회")
@RestController
@RequestMapping("/concerts")
public class ConcertController {

    @Operation(summary = "콘서트 목록 조회")
    @GetMapping("")
    public ResponseEntity<List<ConcertResponse>> getConcerts() {
        return ResponseEntity.ok(
            List.of(
                new ConcertResponse(1L, "콘서트명1", LocalDate.of(2024,12,31), LocalDate.of(2025,1,1)),
                new ConcertResponse(2L, "콘서트명2", LocalDate.of(2025,1,1), LocalDate.of(2025,1,2))
            )
        );
    }

    @Operation(summary = "콘서트 상세 조회")
    @GetMapping("/{concertId}")
    public ResponseEntity<ConcertResponse> findConcert(@PathVariable("concertId") Long concertId) {

        if (concertId != 1L) {
            throw new ConcertException("등록되지 않은 콘서트입니다.");
        }

        return ResponseEntity.ok(
            new ConcertResponse(concertId, "콘서트명1", LocalDate.of(2024,12,31), LocalDate.of(2025,1,1))
        );
    }

    @Operation(summary = "콘서트 예약 가능 날짜 조회", security = @SecurityRequirement(name = "queue-token"))
    @GetMapping("/{concertId}/schedules")
    public ResponseEntity<ConcertScheduleResponse> findConcertSchedule(
            @RequestHeader HttpHeaders headers,
            @PathVariable("concertId") Long concertId) {

        String token = headers.getFirst("Authorization");
        if (token == null) {
            throw new UnAuthorizationException("토큰 정보가 누락되었습니다.");
        }

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token.substring(7);
        } else {
            throw new TokenException("잘못된 토큰입니다.");
        }

        return ResponseEntity.ok(
            new ConcertScheduleResponse(
                concertId,
                List.of(
                    new ScheduleResponse(1L, LocalDate.of(2024,12,31)),
                    new ScheduleResponse(2L, LocalDate.of(2025,1,1))
                )
            )
        );
    }

    @GetMapping("/{concertId}/schedules/{scheduleId}/seats")
    public ResponseEntity<ScheduleSeatResponse> getAvailableSeats(
            @RequestHeader HttpHeaders headers,
            @PathVariable("concertId") Long concertId,
            @PathVariable("scheduleId") Long scheduleId) {

        String token = headers.getFirst("Authorization");
        if (token == null) {
            throw new UnAuthorizationException("토큰 정보가 누락되었습니다.");
        }

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            token.substring(7);
        } else {
            throw new TokenException("잘못된 토큰입니다.");
        }

        return ResponseEntity.ok(
                new ScheduleSeatResponse(
                        scheduleId,
                        List.of(
                                new SeatResponse(1L, "A1", 75000, "Y"),
                                new SeatResponse(2L, "B1", 60000, "N")
                        )
                )
        );
    }

}
