package com.tarikturkdil.dto;

import lombok.Data;

@Data
public class AuthResponse {

	private String username;
	
    private String token;
	
	private String refreshToken;
}
