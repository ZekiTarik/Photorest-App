package com.tarikturkdil.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PinCreateRequest {
	
	@NotBlank(message = "Başlık boş bırakılamaz")
    private String title;

    private String description;

    @NotNull(message = "Fotoğraf yüklemeden pin oluşturamazsınız")
    private MultipartFile image;
}
