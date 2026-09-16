package com.tarikturkdil.dto;

import lombok.Data;


//Herkesin görebileceği profil (başka kullanıcının profiline bakarken)
@Data
public class UserResponse extends DtoBase{

	private String username;
	
	private String avatarUrl;
	
    private String bio;
}
