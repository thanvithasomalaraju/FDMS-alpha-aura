package com.madfood.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Very small in-memory IP rate limiter for basic protection during early staging.
 * Limits requests per IP to a fixed number per sliding window.
 * Not suitable for distributed or heavy-traffic production.
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);

    // requests allowed per window
    private static final int MAX_REQUESTS = 120; // per window
    private static final long WINDOW_MS = 60_000L; // 1 minute window

    private final Map<String, Window> ipWindows = new ConcurrentHashMap<>();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = extractClientIp(request);
        long now = Instant.now().toEpochMilli();

        Window window = ipWindows.computeIfAbsent(ip, k -> new Window(now, 0));

        synchronized (window) {
            if (now - window.windowStart >= WINDOW_MS) {
                window.windowStart = now;
                window.count = 0;
            }
            if (window.count >= MAX_REQUESTS) {
                logger.warn("Rate limit exceeded for ip={}", ip);
                response.setStatus(429);
                response.setHeader("Retry-After", "60");
                response.setContentType("application/json");
                response.getWriter().write("{\"success\":false,\"message\":\"Too many requests\"}");
                return;
            }
            window.count++;
        }

        filterChain.doFilter(request, response);
    }

    private String extractClientIp(HttpServletRequest request) {
        String xf = request.getHeader("X-Forwarded-For");
        if (xf != null && !xf.isBlank()) {
            return xf.split(",")[0].trim();
        }
        String forwarded = request.getHeader("Forwarded");
        if (forwarded != null && forwarded.startsWith("for=")) {
            return forwarded.split(";")[0].replace("for=", "").trim();
        }
        return request.getRemoteAddr();
    }

    private static class Window {
        long windowStart;
        int count;

        Window(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
