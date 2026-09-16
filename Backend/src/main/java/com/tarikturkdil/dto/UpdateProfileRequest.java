package com.tarikturkdil.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;


//Genel profil bilgisi güncelleme (email/password hariç)
@Data
public class UpdateProfileRequest {
	@NotEmpty
    @Size(min = 2, max = 50)
    private String username;

    private String bio;
    private String avatarUrl;
}
