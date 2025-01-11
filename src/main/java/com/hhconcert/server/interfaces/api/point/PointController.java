package com.hhconcert.server.interfaces.api.point;

import com.hhconcert.server.interfaces.api.point.dto.PointRequest;
import com.hhconcert.server.interfaces.api.point.dto.PointResponse;
import com.hhconcert.server.interfaces.facade.UserPointFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "포인트 API", description = "포인트 충전 및 조회")
@RestController
@RequestMapping("/points")
@RequiredArgsConstructor
public class PointController {

    private final UserPointFacade userPointFacade;

    @Operation(summary = "포인트 충전")
    @PatchMapping("")
    public ResponseEntity<PointResponse> chargePoint(@RequestBody PointRequest request) {
        return ResponseEntity.ok(userPointFacade.chargePoint(request));
    }

    @Operation(summary = "포인트 조회")
    @GetMapping("/{userId}")
    public ResponseEntity<PointResponse> getPoint(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(userPointFacade.getPoint(userId));
    }

}
