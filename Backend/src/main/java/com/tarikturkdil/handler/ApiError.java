package com.tarikturkdil.handler;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiError<E> { //Bu sınıf, Kotlin'e dönecek asıl büyük zarftır. İçine HTTP statü kodunu (Örn: 400, 404) 
	                       //ve az önce oluşturduğun ExceptionDetails nesnesini alır.
	
	private Integer status;
	
	private ExceptionDetails<E> exception;

}
