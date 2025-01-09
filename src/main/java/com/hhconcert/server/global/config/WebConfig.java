package com.hhconcert.server.global.config;

import com.hhconcert.server.global.interceptor.TokenValidationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final TokenValidationInterceptor interceptor;
    private final List<String> requirePath = List.of(
            "/concerts/*/schedules",
            "/concerts/schedules/*/seats",
            "/queues/*",
            "/reservations",
            "/payments"
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns(requirePath)
                .excludePathPatterns("/mock/**");
    }
}
