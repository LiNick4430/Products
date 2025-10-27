package com.github.lianick.exception;

public class MailSendFailureException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MailSendFailureException(String message) {
		super(message);
	}
	
	public MailSendFailureException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
