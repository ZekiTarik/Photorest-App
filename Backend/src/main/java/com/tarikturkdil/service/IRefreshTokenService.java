package com.tarikturkdil.service;

import java.util.Optional;

public interface IRefreshTokenService {

    String createRefreshToken(String userEmail);

    Optional<String> getUserEmailByToken(String token);

    void deleteToken(String token);
}