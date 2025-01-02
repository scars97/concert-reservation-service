package com.hhconcert.server.interfaces.api.schedule;

import com.hhconcert.server.exception.TokenException;
import com.hhconcert.server.exception.UnAuthorizationException;
import com.hhconcert.server.interfaces.api.schedule.dto.ScheduleSeatResponse;
import com.hhconcert.server.interfaces.api.schedule.dto.SeatResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    @GetMapping("/{scheduleId}/seats")
    public ResponseEntity<ScheduleSeatResponse> getSeats(
            @RequestHeader HttpHeaders headers,
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
