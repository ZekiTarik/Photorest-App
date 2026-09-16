package com.tarikturkdil.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SendMessageRequest {

    @NotBlank(message = "Mesaj boş bırakılamaz")
    @Size(max = 2000, message = "Mesaj en fazla 2000 karakter olabilir")
    private String content;
}