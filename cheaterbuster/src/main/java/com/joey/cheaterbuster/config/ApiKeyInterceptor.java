package com.joey.cheaterbuster.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
@Component
public class ApiKeyInterceptor implements HandlerInterceptor {

    private static final String API_KEY_HEADER = "X-API-Key";

    @Value("${app.api.key}")
    private String apiKey;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Skip authentication for actuator endpoints
        String requestPath = request.getRequestURI();
        if (requestPath.startsWith("/actuator")) {
            return true;
        }

        // Skip authentication if no API key is configured (for local development)
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("No API key configured - authentication disabled. Set APP_API_KEY environment variable for production.");
            return true;
        }

        String providedApiKey = request.getHeader(API_KEY_HEADER);

        if (providedApiKey == null || providedApiKey.isEmpty()) {
            log.warn("Request to {} rejected - missing API key", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Missing API key. Include X-API-Key header.\"}");
            return false;
        }

        if (!apiKey.equals(providedApiKey)) {
            log.warn("Request to {} rejected - invalid API key", requestPath);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Invalid API key.\"}");
            return false;
        }

        log.debug("Request to {} authenticated successfully", requestPath);
        return true;
    }
}