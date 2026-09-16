package com.tarikturkdil.handler;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionDetails<E> { //Hatanın nerede ve ne zaman olduğunu tutan en küçük veri kümesidir

	private String path;
	
	private LocalDateTime createTime;
	
	private String hostName;
	
	private E message;
}
