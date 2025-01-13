package com.hhconcert.server.interfaces.facade;

import com.hhconcert.server.business.domain.user.service.UserService;
import com.hhconcert.server.interfaces.api.point.dto.PointRequest;
import com.hhconcert.server.interfaces.api.point.dto.PointResponse;
import com.hhconcert.server.interfaces.api.point.dto.UserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserPointFacade {

    private final UserService userService;

    public PointResponse chargePoint(PointRequest request) {
        return PointResponse.from(userService.chargePoint(request.toInfo()));
    }

    public PointResponse getPoint(UserRequest request) {
        return PointResponse.from(userService.findUser(request.userId()));
    }
}
