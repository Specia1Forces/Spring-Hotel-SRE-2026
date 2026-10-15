package com.booking.hotel.config.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class HttpRequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log =
            LoggerFactory.getLogger(HttpRequestLoggingFilter.class);

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long startedAt = System.nanoTime();

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = TimeUnit.NANOSECONDS.toMillis(
                    System.nanoTime() - startedAt
            );
            int status = response.getStatus();

            var event = status >= 500
                    ? log.atError()
                    : status >= 400
                      ? log.atWarn()
                      : log.atInfo();

            event
                    .addKeyValue("http.method", request.getMethod())
                    .addKeyValue("http.path", request.getRequestURI())
                    .addKeyValue("http.status", status)
                    .addKeyValue("duration_ms", durationMs)
                    .addKeyValue(
                            "authenticated",
                            request.getUserPrincipal() != null
                    )
                    .log("HTTP request completed");
        }
    }
}
