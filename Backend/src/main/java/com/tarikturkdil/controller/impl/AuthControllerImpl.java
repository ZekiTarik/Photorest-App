package com.tarikturkdil.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tarikturkdil.controller.IAuthController;
import com.tarikturkdil.controller.RestBaseController;
import com.tarikturkdil.controller.RootEntity;
import com.tarikturkdil.dto.AuthRequest;
import com.tarikturkdil.dto.AuthResponse;
import com.tarikturkdil.dto.RefreshTokenRequest;
import com.tarikturkdil.dto.RegisterRequest;
import com.tarikturkdil.dto.UserResponse;
import com.tarikturkdil.service.IAuthService;

import jakarta.validation.Valid;

@RestController
public class AuthControllerImpl extends RestBaseController implements IAuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/register")
    @Override
    public RootEntity<UserResponse> register(@Valid @RequestBody RegisterRequest input) {
        UserResponse response = authService.register(input);

        // RestBaseController'dan gelen ok() metodunu kullanarak RootEntity zarfına koyuyoruz
        return ok(response);
    }

    @PostMapping("/authenticate")
    @Override
    public RootEntity<AuthResponse> authenticate(@Valid @RequestBody AuthRequest input) {
        AuthResponse response = authService.authenticate(input);
        return ok(response);
    }

    @PostMapping("/refreshToken")
    @Override
    public RootEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest input) {
        AuthResponse response = authService.refreshToken(input);
        return ok(response);
    }

    // Çıkış yapmak için token gerekmeli, bu yüzden SecurityConfig içinde izin (permitAll) VERMEDİK.
    // Yani sadece giriş yapmış (token'ı olan) kullanıcılar istek atıp çıkış yapabilir.
    @PostMapping("/logout")
    @Override
    public RootEntity<String> logout(@Valid @RequestBody RefreshTokenRequest input) {
        authService.logout(input);
        return ok("Çıkış işlemi başarıyla gerçekleştirildi.");
    }
}