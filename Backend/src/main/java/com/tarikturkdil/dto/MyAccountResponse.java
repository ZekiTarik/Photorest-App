package com.tarikturkdil.dto;

import lombok.Data;


//Sadece "benim profilim" endpoint'i (/api/users/me gibi)
@Data
public class MyAccountResponse extends DtoBase{

	private String username;
    private String email;
    private String avatarUrl;
    private String bio;
}
