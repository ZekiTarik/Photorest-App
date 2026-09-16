package com.tarikturkdil.service;

import com.tarikturkdil.dto.AuthRequest;
import com.tarikturkdil.dto.AuthResponse;
import com.tarikturkdil.dto.RefreshTokenRequest;
import com.tarikturkdil.dto.RegisterRequest;
import com.tarikturkdil.dto.UserResponse;

public interface IAuthService {

	public UserResponse register(RegisterRequest input);
	
	public AuthResponse authenticate(AuthRequest input);
	
	public AuthResponse refreshToken(RefreshTokenRequest input);
	
	public void logout(RefreshTokenRequest input);
}
