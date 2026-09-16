package com.tarikturkdil.controller;

import com.tarikturkdil.dto.AuthRequest;
import com.tarikturkdil.dto.AuthResponse;
import com.tarikturkdil.dto.RefreshTokenRequest;
import com.tarikturkdil.dto.RegisterRequest;
import com.tarikturkdil.dto.UserResponse;

public interface IAuthController {

    public RootEntity<UserResponse> register(RegisterRequest input);
    
    public RootEntity<AuthResponse> authenticate(AuthRequest input);
    
    public RootEntity<AuthResponse> refreshToken(RefreshTokenRequest input);
    
    public RootEntity<String> logout(RefreshTokenRequest input);
}
