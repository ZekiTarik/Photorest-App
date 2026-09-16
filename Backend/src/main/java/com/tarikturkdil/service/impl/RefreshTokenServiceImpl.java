package com.tarikturkdil.service.impl;

import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.tarikturkdil.service.IRefreshTokenService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RefreshTokenServiceImpl implements IRefreshTokenService {

    private static final String KEY_PREFIX = "refresh-token:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Value("${refresh-token.expiration-days:2}")
    private long refreshTokenExpirationDays;

    @Override
    public String createRefreshToken(String userEmail) {
        String token = UUID.randomUUID().toString();
        String key = KEY_PREFIX + token;

        redisTemplate.opsForValue().set(key, userEmail, Duration.ofDays(refreshTokenExpirationDays));

        log.info("Redis'te yeni refresh token oluşturuldu: user={}", userEmail);
        return token;
    }

    @Override
    public Optional<String> getUserEmailByToken(String token) {
        String value = redisTemplate.opsForValue().get(KEY_PREFIX + token);
        return Optional.ofNullable(value);
    }

    @Override
    public void deleteToken(String token) {
        redisTemplate.delete(KEY_PREFIX + token);
    }
}