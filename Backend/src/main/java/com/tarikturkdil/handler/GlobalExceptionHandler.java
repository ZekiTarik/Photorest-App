package com.tarikturkdil.handler;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.tarikturkdil.exception.BaseException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

	
	@ExceptionHandler(value = {BaseException.class})
	public ResponseEntity<ApiError<?>> handleBaseException(BaseException ex, WebRequest request){
		return ResponseEntity
	            .status(ex.getStatus())
	            .body(createApiError(ex.getMessage(), request, ex.getStatus()));
	}
	
	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public ResponseEntity<ApiError<Map<String, List<String>>>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex, WebRequest request){
		Map<String, List<String>> map = new HashMap<>();
		for(ObjectError objectError : ex.getBindingResult().getAllErrors()) {
			String fieldName = ((FieldError) objectError).getField();
			if(map.containsKey(fieldName)) {
				map.put(fieldName, addValue(map.get(fieldName), objectError.getDefaultMessage()));
			}
			else {
				map.put(fieldName, addValue(new ArrayList<>(), objectError.getDefaultMessage()));
			}
		}
		return ResponseEntity.badRequest().body(createApiError(map, request, HttpStatus.BAD_REQUEST));
	}
	
	@ExceptionHandler(value = {Exception.class})
	public ResponseEntity<ApiError<?>> handleGenericException(Exception ex, WebRequest request){
	    log.error("Beklenmeyen bir hata oluştu: {}", ex.getMessage(), ex); // BUNU EKLE
	    return ResponseEntity.internalServerError()
	            .body(createApiError("Beklenmeyen bir hata oluştu", request, HttpStatus.INTERNAL_SERVER_ERROR));
	}
	
	@ExceptionHandler(value = {MaxUploadSizeExceededException.class})
	public ResponseEntity<ApiError<?>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex, WebRequest request){
	    return ResponseEntity
	            .status(HttpStatus.PAYLOAD_TOO_LARGE)
	            .body(createApiError("Dosya boyutu çok büyük. Lütfen daha küçük bir dosya yükleyin.", request, HttpStatus.PAYLOAD_TOO_LARGE));
	}
	
	private List<String> addValue(List<String> list, String newValue){
		list.add(newValue);
		return list;
	}
	
	private String getHostName() {
		try {
			return Inet4Address.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			log.warn("Host name alınamadı", e);
		}
		return "";
	}
	
	public <E> ApiError<E> createApiError(E message, WebRequest request, HttpStatus status){
		ApiError<E> apiError = new ApiError<>();
		apiError.setStatus(status.value());
		
		ExceptionDetails<E> exception = new ExceptionDetails<>();
		exception.setPath(request.getDescription(false).substring(4));
		exception.setCreateTime(LocalDateTime.now()); // new Date() yerine
		exception.setMessage(message);
		exception.setHostName(getHostName());
		
		apiError.setException(exception);
		return apiError;
	}
}
