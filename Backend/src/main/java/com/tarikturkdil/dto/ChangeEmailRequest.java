package com.tarikturkdil.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


//Email değiştirme — hassas işlem, şifre doğrulaması ister
@Data
public class ChangeEmailRequest {

	@NotEmpty
    @Email
    private String newEmail;

    @NotEmpty
    private String currentPassword; // güvenlik için mevcut şifre istenir
}
