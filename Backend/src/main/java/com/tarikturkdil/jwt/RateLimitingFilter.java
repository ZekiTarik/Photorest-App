package com.tarikturkdil.jwt;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tarikturkdil.exception.MessageType;
import com.tarikturkdil.handler.ApiError;
import com.tarikturkdil.handler.ExceptionDetails;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import com.fasterxml.jackson.databind.SerializationFeature;

@Slf4j
@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private static final int MAX_ATTEMPTS = 5;
    private static final Duration WINDOW = Duration.ofSeconds(60);

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        // Sadece login ve register endpoint'lerini koru
        boolean isProtectedPath = path.equals("/authenticate") || path.equals("/register");

        if (!isProtectedPath) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientIp = getClientIp(request);
        String redisKey = "rate-limit:" + path + ":" + clientIp;

        Long currentCount = redisTemplate.opsForValue().increment(redisKey);

        if (currentCount != null && currentCount == 1L) {
            // Bu anahtar ilk defa oluşturuluyor, TTL'i şimdi ata
            redisTemplate.expire(redisKey, WINDOW);
        }

        if (currentCount != null && currentCount > MAX_ATTEMPTS) {
            log.warn("Rate limit aşıldı: ip={}, path={}, deneme={}", clientIp, path, currentCount);
            writeRateLimitError(response, request);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private String getClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private void writeRateLimitError(HttpServletResponse response, HttpServletRequest request) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());

        ExceptionDetails<String> details = new ExceptionDetails<>();
        details.setPath(request.getRequestURI());
        details.setCreateTime(LocalDateTime.now());
        details.setMessage(MessageType.TOO_MANY_REQUESTS.getMessage());
        details.setHostName(getHostName());

        ApiError<String> apiError = new ApiError<>();
        apiError.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        apiError.setException(details);

        response.getWriter().write(objectMapper.writeValueAsString(apiError));
    }
    
    private String getHostName() {
        try {
            return java.net.Inet4Address.getLocalHost().getHostName();
        } catch (java.net.UnknownHostException e) {
            log.warn("Host name alınamadı", e);
            return "";
        }
    }
}