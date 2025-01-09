package com.hhconcert.server.global.interceptor;

import com.hhconcert.server.business.domain.queues.persistance.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class TokenValidationInterceptor implements HandlerInterceptor {

    private final TokenRepository tokenRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/plain;charset=UTF-8");

        String token = request.getHeader("Authorization");

        if (token == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("토큰 정보가 누락되었습니다.");
            return false;
        }

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            try {
                tokenRepository.findToken(token.substring(7));
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write("등록되지 않은 토큰입니다.");
                return false;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("잘못된 토큰입니다.");
            return false;
        }

        return true;
    }

}
