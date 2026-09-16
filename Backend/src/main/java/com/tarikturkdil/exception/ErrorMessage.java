package com.tarikturkdil.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {
	
	private MessageType messageType;
	
	private String details;
	
	public String prepareErrorMessage() {
		StringBuilder builder = new StringBuilder();//String nesnelerini birleştirmede kullanılır
		builder.append(messageType.getMessage());
		if(this.details != null) {
			builder.append(" : "+details);
		}
		return builder.toString();
	}
}
