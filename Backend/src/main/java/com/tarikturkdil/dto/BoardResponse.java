package com.tarikturkdil.dto;

import lombok.Data;

@Data
public class BoardResponse extends DtoBase{

	private String name;
    private String description;
    private boolean isSecret;
    // İleride panonun sahibini de dönmek istersen UserResponse ekleyebilirsin
}
