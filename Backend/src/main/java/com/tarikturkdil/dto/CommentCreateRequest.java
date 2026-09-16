package com.tarikturkdil.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentCreateRequest {

	@NotBlank(message = "Yorum boş bırakılamaz")
    @Size(max = 2000, message = "Yorum en fazla 2000 karakter olabilir")
    private String text;
}
