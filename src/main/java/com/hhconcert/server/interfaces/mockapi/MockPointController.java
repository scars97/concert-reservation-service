package com.hhconcert.server.interfaces.mockapi;

import com.hhconcert.server.interfaces.api.point.dto.PointRequest;
import com.hhconcert.server.interfaces.api.point.dto.PointResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "포인트 API", description = "포인트 충전 및 조회")
@RestController
@RequestMapping("/mock/points")
public class MockPointController {

    @Operation(summary = "포인트 충전")
    @PatchMapping("")
    public ResponseEntity<PointResponse> chargePoint(@RequestBody PointRequest request) {
        return ResponseEntity.ok(
            new PointResponse(request.userId(), request.amount())
        );
    }

    @Operation(summary = "포인트 조회")
    @GetMapping("/{userId}")
    public ResponseEntity<PointResponse> getPoint(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(
                new PointResponse(userId, 300000)
        );
    }
}
