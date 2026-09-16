package com.tarikturkdil.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

//Şifre değiştirme — hassas işlem
@Data
public class ChangePasswordRequest {

	@NotEmpty
    private String currentPassword;

    @NotEmpty
    @Size(min = 8)
    private String newPassword;
}
