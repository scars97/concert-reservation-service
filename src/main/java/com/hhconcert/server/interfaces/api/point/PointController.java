package com.hhconcert.server.interfaces.api.point;

import com.hhconcert.server.interfaces.api.point.dto.PointRequest;
import com.hhconcert.server.interfaces.api.point.dto.PointResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/points")
public class PointController {

    @PatchMapping("")
    public ResponseEntity<PointResponse> chargePoint(@RequestBody PointRequest request) {
        return ResponseEntity.ok(
            new PointResponse(request.userId(), request.amount())
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<PointResponse> getPoint(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(
                new PointResponse(userId, 300000)
        );
    }
}
