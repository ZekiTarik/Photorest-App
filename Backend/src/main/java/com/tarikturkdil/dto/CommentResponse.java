package com.tarikturkdil.dto;

import lombok.Data;

@Data
public class CommentResponse extends DtoBase {

	private String text;
    private String username; // yorumu yazanın adı, mobil tarafta göstermek için
}
