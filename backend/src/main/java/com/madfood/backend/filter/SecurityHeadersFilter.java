package com.madfood.backend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Adds a small set of security headers to responses.
 * This is intentionally conservative — adjust CSP and other values before production.
 */
@Component
public class SecurityHeadersFilter extends HttpFilter {

    private static final Logger logger = LoggerFactory.getLogger(SecurityHeadersFilter.class);

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // Prevent MIME-type sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");
        // Clickjacking protection
        response.setHeader("X-Frame-Options", "DENY");
        // Basic XSS protection (legacy browsers)
        response.setHeader("X-XSS-Protection", "1; mode=block");
        // Referrer policy
        response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        // Content security policy - slightly stricter but still permissive enough for dev
        // Allows scripts/styles from self and https, permits inline scripts/styles to avoid breaking dev tooling
        response.setHeader("Content-Security-Policy", "default-src 'self' https:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https:; style-src 'self' 'unsafe-inline' https:; img-src 'self' data: https:; object-src 'none'; frame-ancestors 'none';");

        chain.doFilter(request, response);
    }
}
