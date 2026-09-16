package com.tarikturkdil.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class BoardCreateRequest {

	@NotBlank(message = "Pano adı boş bırakılamaz")
	@Size(max = 100, message = "Pano adı en fazla 100 karakter olabilir")
    private String name;
    
	@Size(max = 500, message = "Açıklama en fazla 500 karakter olabilir")
    private String description;
    
    // Kullanıcı belirtmezse varsayılan olarak public (false) olsun
    private boolean isSecret = false;
}
