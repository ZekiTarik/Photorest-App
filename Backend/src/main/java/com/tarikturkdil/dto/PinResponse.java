package com.tarikturkdil.dto;

import lombok.Data;

@Data
public class PinResponse extends DtoBase{
	private String title;
    private String description;
    private String imageUrl;
    private String ownerUsername;
    // İleride pinin sahibini de dönmek istersen UserResponse ekleyebilirsin
    private boolean likedByMe;
    private boolean savedByMe;
}
