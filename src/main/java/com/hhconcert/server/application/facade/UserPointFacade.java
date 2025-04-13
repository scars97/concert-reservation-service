package com.hhconcert.server.application.facade;

import com.hhconcert.server.application.dto.PointResult;
import com.hhconcert.server.business.user.service.UserService;
import com.hhconcert.server.interfaces.api.point.dto.PointRequest;
import com.hhconcert.server.interfaces.api.point.dto.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPointFacade {

    private final UserService userService;

    public PointResult chargePoint(PointRequest request) {
        return PointResult.from(userService.chargePoint(request.toCommand()));
    }

    public PointResult getPoint(UserRequest request) {
        return PointResult.from(userService.findUser(request.userId()));
    }
}
