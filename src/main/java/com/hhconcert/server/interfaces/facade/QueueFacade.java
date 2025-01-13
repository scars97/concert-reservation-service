package com.hhconcert.server.interfaces.facade;

import com.hhconcert.server.business.domain.queues.dto.TokenResult;
import com.hhconcert.server.business.domain.queues.service.TokenService;
import com.hhconcert.server.business.domain.user.dto.UserResult;
import com.hhconcert.server.business.domain.user.service.UserService;
import com.hhconcert.server.interfaces.api.queues.dto.TokenRequest;
import com.hhconcert.server.interfaces.api.queues.dto.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueFacade {

    private final TokenService tokenService;
    private final UserService userService;

    public TokenResponse createToken(TokenRequest request) {
        UserResult user = userService.findUser(request.userId());

        return TokenResponse.from(tokenService.createToken(user.userId()));
    }

    public TokenResponse checkQueueStatus(TokenRequest request) {
        TokenResult result = tokenService.checkQueueStatus(request.userId());
        return TokenResponse.from(result);
    }

}
