package com.tarikturkdil.dto;

import lombok.Data;

@Data
public class SavedPinResponse extends DtoBase{

	private PinResponse pin;
    private Long boardId;
}
