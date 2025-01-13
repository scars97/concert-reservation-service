package com.hhconcert.server.global.interceptor;

import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenValidationInterceptor implements HandlerInterceptor {

    private final TokenRepository tokenRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");

        log.info("Received request: URI={}, Method={}, Token={}",
                request.getRequestURI(),
                request.getMethod(),
                token != null ? token : "No Token");

        if (token == null) {
            log.warn("Authorization header is missing.");
            return errorResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "토큰 정보가 누락되었습니다.");
        }

        if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
            log.warn("Invalid token format: {}", token);
            return errorResponse(response, HttpServletResponse.SC_FORBIDDEN, "잘못된 토큰입니다.");
        }

        try {
            String extractedToken = token.substring(7);
            log.debug("Extracted Token: {}", extractedToken);
            tokenRepository.findToken(extractedToken);
        } catch (RuntimeException e) {
            log.error("Token not found: {}", token, e);
            return errorResponse(response, HttpServletResponse.SC_NOT_FOUND, "등록되지 않은 토큰입니다.");
        }

        return true;
    }

    private boolean errorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        log.error("Responding with error: Status={}, Message={}", statusCode, message);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");
        response.setStatus(statusCode);
        response.getWriter().write(message);
        return false;
    }

}
