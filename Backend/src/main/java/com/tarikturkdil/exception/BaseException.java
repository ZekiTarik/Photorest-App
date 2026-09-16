package com.tarikturkdil.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException{

	private final ErrorMessage errorMessage;

    public BaseException(ErrorMessage errorMessage) {
        super(errorMessage.prepareErrorMessage());
        this.errorMessage = errorMessage;
    }

    public HttpStatus getStatus() {
        return errorMessage.getMessageType().getStatus();
    }

    public ErrorMessage getErrorMessage() {
        return errorMessage;
    }
}
