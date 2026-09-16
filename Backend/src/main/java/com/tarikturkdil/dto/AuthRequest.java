package com.tarikturkdil.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class AuthRequest {//SADECE GİRİS İCİN

	@NotEmpty
	private String email;
	
	@NotEmpty
	private String password;
}
