package com.hhconcert.server.global.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Slf4j
@Component
public class LoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        logRequest(httpRequest);

        chain.doFilter(request, response);

        logResponse(httpResponse);
    }

    private void logRequest(HttpServletRequest request) {
        String requestLog = "Request: " +
                "[Method: " + request.getMethod() + "] " +
                "[URI: " + request.getRequestURI() + "] " +
                "[Headers: " + getHeaders(request) + "] " +
                "[Query Params: " + request.getQueryString() + "]";

        log.info(requestLog);
    }

    private void logResponse(HttpServletResponse response) {
        String responseLog = "Response: " +
                "[Status: " + response.getStatus() + "]";

        log.info(responseLog);
    }

    private String getHeaders(HttpServletRequest request) {
        StringBuilder headers = new StringBuilder();
        request.getHeaderNames().asIterator().forEachRemaining(headerName -> {
            headers.append(headerName).append(": ").append(request.getHeader(headerName)).append(", ");
        });
        return headers.toString();
    }
}
