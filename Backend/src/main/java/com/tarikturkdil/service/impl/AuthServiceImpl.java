package com.tarikturkdil.service.impl;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.tarikturkdil.dto.AuthRequest;
import com.tarikturkdil.dto.AuthResponse;
import com.tarikturkdil.dto.RefreshTokenRequest;
import com.tarikturkdil.dto.RegisterRequest;
import com.tarikturkdil.dto.UserResponse;
import com.tarikturkdil.entity.User;
import com.tarikturkdil.exception.BaseException;
import com.tarikturkdil.exception.ErrorMessage;
import com.tarikturkdil.exception.MessageType;
import com.tarikturkdil.jwt.JwtService;
import com.tarikturkdil.repository.UserRepository;
import com.tarikturkdil.service.IAuthService;
import com.tarikturkdil.service.IRefreshTokenService;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IRefreshTokenService refreshTokenService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    // --- 1. KAYIT İŞLEMİ (REGISTER) ---
    @Override
    @Transactional
    public UserResponse register(RegisterRequest input) {

        Optional<User> optUser = userRepository.findByEmail(input.getEmail());
        if (optUser.isPresent()) {
            throw new BaseException(
                new ErrorMessage(MessageType.EMAIL_ALREADY_EXISTS, null)
            );
        }

        User user = new User();
        user.setName(input.getUsername());
        user.setEmail(input.getEmail());
        user.setPassword(passwordEncoder.encode(input.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("Yeni kullanıcı kaydedildi: {}", savedUser.getEmail());

        UserResponse userResponse = new UserResponse();
        BeanUtils.copyProperties(savedUser, userResponse);

        return userResponse;
    }

    // --- 2. GİRİŞ İŞLEMİ (AUTHENTICATE) ---
    @Override
    @Transactional
    public AuthResponse authenticate(AuthRequest input) {

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(input.getEmail(), input.getPassword())
            );
        } catch (AuthenticationException e) {
            log.warn("Başarısız giriş denemesi: {}", input.getEmail());
            throw new BaseException(
                new ErrorMessage(MessageType.USERNAME_OR_PASSWORD_INVALID, null)
            );
        }

        User user = (User) authentication.getPrincipal();

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        log.info("Kullanıcı giriş yaptı: {}", user.getEmail());

        AuthResponse response = new AuthResponse();
        response.setUsername(user.getName());
        response.setToken(jwtToken);
        response.setRefreshToken(refreshToken);

        return response;
    }

    // --- 3. TOKEN YENİLEME (REFRESH TOKEN) ---
    @Override
    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest input) {

        String userEmail = refreshTokenService.getUserEmailByToken(input.getRefreshToken())
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage(MessageType.REFRESH_TOKEN_NOT_FOUND, null)
                ));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage(MessageType.USERNAME_NOT_FOUND, null)
                ));

        // Rotation: eski token silinir, yenisi üretilir
        refreshTokenService.deleteToken(input.getRefreshToken());
        String newJwtToken = jwtService.generateToken(user);
        String newRefreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        log.info("Token yenilendi: {}", user.getEmail());

        AuthResponse response = new AuthResponse();
        response.setUsername(user.getName());
        response.setToken(newJwtToken);
        response.setRefreshToken(newRefreshToken);

        return response;
    }

    // --- 4. GÜVENLİ ÇIKIŞ (LOGOUT) ---
    @Override
    @Transactional
    public void logout(RefreshTokenRequest input) {
        String currentEmail = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<String> tokenOwnerEmail = refreshTokenService.getUserEmailByToken(input.getRefreshToken());

        if (tokenOwnerEmail.isPresent()) {
            if (!tokenOwnerEmail.get().equals(currentEmail)) {
                log.warn("Yetkisiz logout denemesi: {} kullanıcısı başka birinin token'ını silmeye çalıştı.", currentEmail);
                throw new BaseException(new ErrorMessage(MessageType.USERNAME_OR_PASSWORD_INVALID, null));
            }

            refreshTokenService.deleteToken(input.getRefreshToken());
            log.info("Kullanıcı çıkış yaptı: {}", currentEmail);
        } else {
            log.warn("Logout denemesi: refresh token bulunamadı.");
        }
    }
}