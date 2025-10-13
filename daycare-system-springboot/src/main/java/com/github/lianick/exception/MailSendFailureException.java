package com.github.lianick.exception;

public class MailSendFailureException extends RuntimeException{

	public MailSendFailureException(String message) {
		super(message);
	}
	
	public MailSendFailureException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
