package com.hhconcert.server.global.interceptor;

import com.hhconcert.server.business.domain.queues.entity.TokenGenerator;
import com.hhconcert.server.business.domain.queues.entity.TokenStatus;
import com.hhconcert.server.business.domain.queues.entity.TokenVO;
import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import com.hhconcert.server.global.common.error.ErrorCode;
import com.hhconcert.server.global.common.exception.BusinessException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenValidationInterceptor implements HandlerInterceptor {

    private final TokenRepository tokenRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = request.getHeader("Authorization");

        log.info("Received request: URI={}, Method={}", request.getRequestURI(), request.getMethod());

        if (token == null) {
            throw new BusinessException(ErrorCode.TOKEN_IS_MISSING);
        }

        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        TokenVO findToken;
        try {
            String extractedToken = token.substring(7);
            String restoreUserId = TokenGenerator.tokenIdToUserId(extractedToken);
            findToken = tokenRepository.findTokenBy(restoreUserId);
        } catch (RuntimeException e) {
            throw new BusinessException(ErrorCode.UNREGISTERED_TOKEN);
        }

        if (findToken != null && findToken.status() != TokenStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.TOKEN_IS_UNAVAILABLE);
        }

        return true;
    }

}
