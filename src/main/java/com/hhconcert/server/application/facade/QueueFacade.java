package com.hhconcert.server.application.facade;

import com.hhconcert.server.application.dto.TokenResult;
import com.hhconcert.server.business.domain.queues.dto.TokenInfo;
import com.hhconcert.server.business.domain.queues.service.TokenService;
import com.hhconcert.server.business.domain.user.dto.UserInfo;
import com.hhconcert.server.business.domain.user.service.UserService;
import com.hhconcert.server.interfaces.api.queues.dto.TokenRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class QueueFacade {

    private final TokenService tokenService;
    private final UserService userService;

    public TokenResult createToken(TokenRequest request) {
        UserInfo user = userService.findUser(request.userId());

        TokenInfo createdToken = tokenService.createToken(user.userId());

        return TokenResult.from(tokenService.checkQueueStatus(createdToken));
    }

    public TokenResult checkQueueStatus(TokenRequest request) {
        TokenInfo tokenInfo = tokenService.findTokenBy(request.userId());

        return TokenResult.from(tokenService.checkQueueStatus(tokenInfo));
    }

}
