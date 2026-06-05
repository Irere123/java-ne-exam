package com.example.javaexam.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Rejects a request whose declared body exceeds {@code app.max-request-bytes}
 * up front with a {@code 413}, so an oversized payload is never read into
 * memory. None of this system's JSON payloads come close to the default 1&nbsp;MB
 * limit. Requests without a {@code Content-Length} (chunked) are passed through.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestSizeLimitFilter extends OncePerRequestFilter {

    @Value("${app.max-request-bytes:1048576}")
    private long maxBytes;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {
        if (request.getContentLengthLong() > maxBytes) {
            response.setStatus(HttpStatus.PAYLOAD_TOO_LARGE.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            // Fixed, non-user-controlled content — safe to assemble as a JSON literal.
            response.getWriter().write("{\"message\":\"Request body exceeds the " + maxBytes
                    + "-byte limit\",\"timestamp\":\"" + LocalDateTime.now() + "\"}");
            return;
        }
        chain.doFilter(request, response);
    }
}
