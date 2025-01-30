package com.hhconcert.server.interfaces.api.point;

import com.hhconcert.server.application.facade.UserPointFacade;
import com.hhconcert.server.interfaces.api.point.dto.PointRequest;
import com.hhconcert.server.interfaces.api.point.dto.PointResponse;
import com.hhconcert.server.interfaces.api.point.dto.UserRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PointResponse.class)
        )
    )
    @PatchMapping("")
    public ResponseEntity<PointResponse> chargePoint(@Valid @RequestBody PointRequest request) {
        return ResponseEntity.ok(PointResponse.from(userPointFacade.chargePoint(request)));
    }

    @Operation(summary = "포인트 조회")
    @ApiResponse(responseCode = "200", description = "OK",
        content = @Content(
            mediaType = "application/json",
            schema = @Schema(implementation = PointResponse.class)
        )
    )
    @GetMapping("/{userId}")
    public ResponseEntity<PointResponse> getPoint(@Valid UserRequest request) {
        return ResponseEntity.ok(PointResponse.from(userPointFacade.getPoint(request)));
    }

}
