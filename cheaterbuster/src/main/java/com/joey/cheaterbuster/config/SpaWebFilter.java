package com.joey.cheaterbuster.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filter to support React Router (SPA routing).
 * Forwards all non-API, non-static-file requests to index.html
 * so React Router can handle client-side routing.
 */
@Component
public class SpaWebFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String path = httpRequest.getRequestURI();

        // Skip API endpoints - let them through to controllers
        if (path.startsWith("/api/") || path.startsWith("/actuator/")) {
            chain.doFilter(request, response);
            return;
        }

        // Skip requests for static resources (has file extension)
        if (path.contains(".")) {
            chain.doFilter(request, response);
            return;
        }

        // For all other requests (React routes), forward to index.html
        // This allows React Router to handle the routing on the client side
        Resource resource = new ClassPathResource("static/index.html");
        if (resource.exists()) {
            request.getRequestDispatcher("/index.html").forward(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}
